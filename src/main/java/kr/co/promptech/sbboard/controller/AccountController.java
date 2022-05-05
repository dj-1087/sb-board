package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccountController {
    @GetMapping("/sign-up")
    public String signUpView(Model model) {
        model.addAttribute("accountDto", new AccountDto());
        return "app/auth/signUp";
    }
}
