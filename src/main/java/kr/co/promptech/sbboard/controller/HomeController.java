package kr.co.promptech.sbboard.controller;


import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "/app/home";
    }
}
