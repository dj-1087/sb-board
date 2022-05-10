package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.dto.FileDto;
import kr.co.promptech.sbboard.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value="/upload")
    public ResponseEntity<?> upload(MultipartFile[] files) {
        try {
            List<FileDto> fileDtoList = fileService.store(files);
            return ResponseEntity.ok().body(fileDtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
