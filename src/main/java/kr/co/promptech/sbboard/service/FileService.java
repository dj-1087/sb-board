package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.dto.FileDto;
import kr.co.promptech.sbboard.model.enums.FileType;
import kr.co.promptech.sbboard.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final ModelMapper modelMapper;

    @Value("${spring.servlet.multipart.location}")
    private String UPLOAD_PATH;

    public List<FileDto> store(MultipartFile[] files) {
        try {
            if (files==null || files.length == 0) {
                throw new Exception("ERROR : File is empty.");
            }

            List<FileDto> fileDtoList = new ArrayList<>();
            for (MultipartFile multipartFile : files) {
                log.info("================multipartFile.getContentType()================");
                log.info(multipartFile.getContentType());

                String typePath = FileType.classifyPath(multipartFile.getContentType());
                log.info(typePath);

                String path = String.format("%s%s/", UPLOAD_PATH, typePath);
                this.initDir(path);

                String fileName = multipartFile.getOriginalFilename();
                if (fileName == null) {
                    // TODO: 추후 제대로 에러처리
                    throw new Exception();
                }
                String extension = fileName.split("\\.")[fileName.split("\\.").length - 1];
                File file = File.builder().name(fileName).path(path).ext(extension).build();
                this.uploadFile(multipartFile, file);

                FileDto fileDto = modelMapper.map(file, FileDto.class);
                fileDtoList.add(fileDto);
            }
            return fileDtoList;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public void uploadFile(MultipartFile multipartFile, File file) {
        try {
            Files.copy(multipartFile.getInputStream(), Paths.get(file.getPath() + file.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public void initDir(String dirPath) {
        try {
            Path root = Paths.get(dirPath);
            if (!Files.exists(root))
                Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder");
        }
    }
}
