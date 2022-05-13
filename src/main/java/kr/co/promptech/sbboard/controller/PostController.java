package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.vo.PostVo;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.service.FileService;
import kr.co.promptech.sbboard.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileService fileService;

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
}
