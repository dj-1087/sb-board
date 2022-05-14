package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.dto.FileDto;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.response.PostFileResponse;
import kr.co.promptech.sbboard.model.vo.PostVo;
import kr.co.promptech.sbboard.service.FileService;
import kr.co.promptech.sbboard.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Value("${spring.servlet.multipart.location}")
    private String UPLOAD_PATH;

    @GetMapping("")
    public String newPost(Model model) {
        model.addAttribute("postVo", new PostVo());

        return "app/post/new";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "redirect:/";
        }

        model.addAttribute(post);

        return "app/post/show";
    }

    @PostMapping(value = "")
    public String create(@CurrentUser Account account,
                         @ModelAttribute("postVo") @Valid PostVo postVo,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.info("========has errors========");
            return "app/post/new";
        }

        Post post = postService.save(postVo, account);

        try {
            log.info("before save");
            log.info(postVo.getFiles().get(0).getOriginalFilename());
            fileService.save(postVo.getFiles(), post);
        } catch (Exception e) {
            return "app/post/new";
        }

        return "redirect:/";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id);
        if (post == null) {
            // TODO: 추후 제대로 처리
            return "redirect:/";
        }

        PostVo postVo = modelMapper.map(post, PostVo.class);
        model.addAttribute(postVo);

        return "app/post/edit";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        postService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<?> getFiles(@PathVariable("id") Long id) {
        Post post = postService.findById(id);
        List<File> fileList = new ArrayList<>(post.getFileSet());
        List<FileDto> fileDtoList = modelMapper.map(fileList,
                new TypeToken<List<FileDto>>() {
                }.getType());

        return ResponseEntity.ok().body(fileDtoList);
    }

    @PutMapping("/{id}")
    public String update( @ModelAttribute("postVo") @Valid PostVo postVo,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("========has errors========");
            return "app/post/edit";
        }

        Post post = postService.update(postVo);
        try {
            log.info("before save");
            log.info(postVo.getFiles().get(0).getOriginalFilename());
            fileService.save(postVo.getFiles(), post);
        } catch (Exception e) {
            return "app/post/edit";
        }

        return "redirect:/posts/" + post.getId();
    }

}
