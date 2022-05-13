package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Value("${spring.servlet.multipart.location}")
    private String UPLOAD_PATH;

    @GetMapping("/{id}")
    public ResponseEntity<?> download(@PathVariable("id") Long id) {
        File fileInfo = fileService.findById(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        String filePath = String.format("%s/%s/%s", UPLOAD_PATH, fileInfo.getPost().getId(), fileInfo.getName());

        java.io.File file = new java.io.File(filePath);
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok().contentLength(file.length()).body(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
