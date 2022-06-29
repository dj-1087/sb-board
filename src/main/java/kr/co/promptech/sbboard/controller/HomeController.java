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

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping("/")
    public String home(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                       @CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        Page<Post> posts = postService.findAll(pageable);

        String url = "/";
        Pagination pagination = new Pagination(posts, pageable, url);

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalCount", posts.getTotalElements());
        model.addAttribute("pagination", pagination);
        return "app/home";
    }

    @GetMapping("/board")
    public String board(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam BoardType type,
                        @CurrentUser Account account, Model model) {

        model.addAttribute(account);

        Page<Post> posts = postService.findAllByBoardType(type, pageable);
        String url = "/board";
        Pagination pagination = new Pagination(posts, pageable, url);
        pagination.addQuery("type",type.getKey());

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalCount", posts.getTotalElements());
        model.addAttribute("pagination", pagination);
        model.addAttribute("menuActive", type);
        return "app/home";
    }

    @GetMapping("/search")
    public String search(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam(value = "keyword") String keyword,
                         @CurrentUser Account account, Model model) {
        model.addAttribute(account);

        Page<Post> posts = postService.search(keyword, pageable);

        String url = "/";
        Pagination pagination = new Pagination(posts, pageable, url);

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalCount", posts.getTotalElements());
        model.addAttribute("pagination", pagination);
        return "app/home";
    }
}
