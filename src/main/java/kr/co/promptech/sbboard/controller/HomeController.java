package kr.co.promptech.sbboard.controller;


import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.enums.BoardType;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.helper.Pagination;
import kr.co.promptech.sbboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping("/")
    public String home(@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                       @CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        Page<Post> posts = postService.findAll(pageable);
//        List<Post> posts = postService.findAll();
        String url = "/";
        Pagination pagination = new Pagination(posts, pageable, url);

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalCount", posts.getTotalElements());
        model.addAttribute("pagination", pagination);
        return "app/home";
    }

    @GetMapping("/board")
    public String board(@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                       @RequestParam BoardType type,
                       @CurrentUser Account account, Model model) {

        model.addAttribute(account);

        Page<Post> posts = postService.findAllByBoardType(type, pageable);
//        List<Post> posts = postService.findAll();
        String url = "/";
        Pagination pagination = new Pagination(posts, pageable, url);

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalCount", posts.getTotalElements());
        model.addAttribute("pagination", pagination);
        return "app/home";
    }
}
