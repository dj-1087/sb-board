package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.dto.PostDto;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public String newPost(Model model) {
        model.addAttribute("postDto", new PostDto());

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

    @PostMapping("")
    public String create(@CurrentUser Account account,
                         @ModelAttribute("postDto") @Valid PostDto postDto,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.info("========has errors========");
            return "app/post/new";
        }

        postService.save(postDto, account);

        return "redirect:/";
    }
}
