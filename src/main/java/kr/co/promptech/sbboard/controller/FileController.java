package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.service.FileService;
import kr.co.promptech.sbboard.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final PostService postService;

    @Value("${spring.servlet.multipart.location}")
    private String UPLOAD_PATH;

    @Value("${upload.relative-path}")
    private String FILE_PATH;

    @GetMapping("/{id}")
    public ResponseEntity<?> download(@PathVariable("id") Long id) {
        File fileInfo = fileService.findById(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        String filePath = String.format("%s/%s/%s", UPLOAD_PATH, fileInfo.getPost().getId(), fileInfo.getName());
        String mimeType = URLConnection.guessContentTypeFromName(filePath);

        java.io.File file = new java.io.File(filePath);

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/summernote")
    public ResponseEntity<?> uploadSummernoteFile(@RequestPart(value = "file", required = true) MultipartFile multipartFile) {

        File file = fileService.generateSummernoteFileInfo(multipartFile);
        fileService.uploadFile(multipartFile, file);

        HashMap<String, String> result = new HashMap<>();

        String url = String.format("%s%s/%s", FILE_PATH, "summernote", file.getName());
        result.put("url", url);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        fileService.delete(id);
        return ResponseEntity.ok().build();
    }



}
