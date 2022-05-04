package kr.co.promptech.sbboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccountController {
    @GetMapping("/sign-up")
    public String signUpView(Model model) {

        return "app/auth/signUp";
    }
}
