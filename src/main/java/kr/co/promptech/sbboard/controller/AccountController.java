package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.dto.AccountDto;
import kr.co.promptech.sbboard.model.parameter.EmailTokenParameter;
import kr.co.promptech.sbboard.model.validator.AccountValidator;
import kr.co.promptech.sbboard.service.AccountService;
import kr.co.promptech.sbboard.util.ResultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountValidator accountValidator;

    @InitBinder("accountDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountValidator);
    }

    @GetMapping("/sign-up")
    public String signUpView(Model model) {
        model.addAttribute("accountDto", new AccountDto());
        return "/app/auth/sign-up";
    }

    @PostMapping("/account")
    public String signUp(@ModelAttribute("accountDto") @Valid AccountDto accountDto,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.info("========has errors========");
            return "/app/auth/sign-up";
        }

        // save account
        Account account = accountService.signUp(accountDto);

        // send mail
        ResultHandler result = accountService.sendConfirmationMail(account);
        if (result.isFailure()) {
            model.addAttribute("error", result.getErrorMessage());
            return "/app/auth/sign-up";
        }

        return "redirect:/";
    }

    @GetMapping("/email-token")
    public String checkEmailToken(EmailTokenParameter parameter, Model model) {
        log.info("=======check email token controller=======");
        log.info(parameter.getEmail());

        Account account = accountService.findByEmail(parameter.getEmail());

        ResultHandler result = accountService.checkExistence(account, parameter);
        if (result.isFailure()) {
            model.addAttribute("error", result.getErrorMessage());
            return "/app/auth/authenticate";
        }

        accountService.updateEmailConfirmed(account);

        model.addAttribute("error", null);
        model.addAttribute("nickname", account.getNickname());
        return "/app/auth/authenticate";
    }
}
