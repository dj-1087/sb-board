package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.dto.AccountDto;
import kr.co.promptech.sbboard.model.validator.AccountValidator;
import kr.co.promptech.sbboard.repository.AccountRepository;
import kr.co.promptech.sbboard.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
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

    private final ModelMapper modelMapper;

    @InitBinder("accountValidator")
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
                         Model model, Errors errors) {
        if (errors.hasErrors()) {
            return "/app/auth/sign-up";
        }
        Account account = modelMapper.map(accountDto, Account.class);
        String token = accountService.generateEmailConfirmToken();
        account.setEmailConfirmToken(token);
        log.info(account.getEmail());
        log.info(account.getEmailConfirmToken());

        // TODO: accountType 에 대해 추후 수정
        account.setAccountType("USER");
        log.info("accountType=" + account.getAccountType());
        Account savedAccount = accountService.save(account);


        return "redirect:/";
    }
}
