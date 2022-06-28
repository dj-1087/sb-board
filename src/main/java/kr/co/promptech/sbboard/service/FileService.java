package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.repository.FileRepository;
import kr.co.promptech.sbboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    @Value("${spring.servlet.multipart.location}")
    private String UPLOAD_PATH;

    public File findById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    public void saveAll(List<MultipartFile> files, Post post) {
        try {
            if (files.size() == 0) {
                throw new Exception("ERROR : File is empty.");
            }

            for (int i = 0; i < files.size(); i++) {
                MultipartFile multipartFile = files.get(i);
                File file = this.generateFileInfo(multipartFile, post.getId(), String.valueOf(i));
                if (file == null) {
                    //TODO: 추후 제대로 에러처리
                    log.info("null file");
                    continue;
                }
                this.uploadFile(multipartFile, file);
                post.addFile(file);
            }

            postRepository.save(post);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }


    public File generateFileInfo(MultipartFile multipartFile, Long postId, String uniqueId) {
        String path = String.format("%s/%s/", UPLOAD_PATH, postId);
        this.initDir(path);

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.equals("")) {
            log.info("===========generateFileInfo: no originalFilename===========");
            log.info(originalFilename);
            // TODO: 추후 제대로 에러처리
            return null;
        }

        String extension = originalFilename.split("\\.")[originalFilename.split("\\.").length - 1];

        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + uniqueId + "." + extension;
        return File.builder()
                .name(fileName)
                .originalName(originalFilename)
                .path(path)
                .ext(extension).build();
    }

    public File generateSummernoteFileInfo(MultipartFile multipartFile) {
        String path = String.format("%s/summernote/", UPLOAD_PATH);
        this.initDir(path);

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.equals("")) {
            log.info("===========generateSummernoteFileInfo: no originalFilename===========");
            log.info(originalFilename);
            // TODO: 추후 제대로 에러처리
            return null;
        }

        String extension = originalFilename.split("\\.")[originalFilename.split("\\.").length - 1];
        UUID uniqueId = UUID.randomUUID();

        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + uniqueId + "." + extension;
        return File.builder()
                .name(fileName)
                .originalName(originalFilename)
                .path(path)
                .ext(extension).build();
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

    public void delete(Long id) {
        fileRepository.deleteById(id);
    }
}
