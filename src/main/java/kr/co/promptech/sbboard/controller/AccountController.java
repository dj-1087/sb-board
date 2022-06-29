package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.dto.AccountDto;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.parameter.EmailTokenParameter;
import kr.co.promptech.sbboard.model.validator.AccountValidator;
import kr.co.promptech.sbboard.model.vo.AccountVo;
import kr.co.promptech.sbboard.service.AccountService;
import kr.co.promptech.sbboard.util.ResultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
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

    private final ModelMapper modelMapper;

    @InitBinder("accountVo")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountValidator);
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception,
                        @RequestParam(value = "errorType", required = false) String errorType,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        model.addAttribute("errorType", errorType);
        model.addAttribute("newLineChar", '\n');
        return "app/auth/login";
    }

    @GetMapping("/sign-up")
    public String signUpView(Model model) {
        model.addAttribute("accountVo", new AccountVo());
        return "app/auth/sign-up";
    }

    @PostMapping("/account")
    public String signUp(@ModelAttribute("accountVo") @Valid AccountVo accountVo,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "app/auth/sign-up";
        }

        // save account
        Account account = accountService.signUp(accountVo);

        // send mail
        ResultHandler result = accountService.sendConfirmationMail(account);
        if (result.isSuccess()) {
            return "redirect:/";

        }

        model.addAttribute("error", result.getErrorMessage());
        return "app/auth/sign-up";
    }

    @GetMapping("/email-token")
    public String checkEmailToken(EmailTokenParameter parameter, Model model) {
        Account account = accountService.findByEmail(parameter.getEmail());

        ResultHandler result = accountService.checkExistence(account, parameter);
        if (result.isSuccess()) {
            accountService.updateEmailConfirmed(account);

            model.addAttribute("error", null);
            model.addAttribute("nickname", account.getNickname());
            return "app/auth/authenticate";
        }

        model.addAttribute("error", result.getErrorMessage());
        return "app/auth/authenticate";
    }

    @GetMapping("/email-token/resend")
    public ResponseEntity<?> resendConfirmationMail(@RequestParam String email) {
        Account account = accountService.findByEmail(email);
        ResultHandler result;
        if (account == null) {
            result = ResultHandler.builder()
                    .isSuccess(false)
                    .errorMessage("해당 메일로 가입된 계정이 없습니다.").build();
            return ResponseEntity.badRequest().body(result);
        }

        account = accountService.resetEmailConfirmToken(account);

        result = accountService.sendConfirmationMail(account);
        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.internalServerError().body(result);
    }

    @GetMapping("/session")
    public ResponseEntity<?> session(@CurrentUser Account account) {
        AccountDto accountDto = modelMapper.map(account, AccountDto.class);
        accountDto.setAdmin(account.isAdmin());

        return ResponseEntity.ok().body(accountDto);
    }

    @GetMapping("/find-email")
    public String findEmailView(Model model) {
        model.addAttribute("accountVo", new AccountVo());
        return "app/auth/find-email";
    }

    @PostMapping("/find-email")
    public String findEmail(@ModelAttribute("accountVo") AccountVo accountVo, Model model) {

        String email = accountService.findEmailByNickname(accountVo.getNickname());
        if (email == null) {
            model.addAttribute("errorMessage", "해당 닉네임으로 가입한 계정이 없습니다.");
        }
        model.addAttribute("email", email);

        return "app/auth/find-email-result";
    }

    @GetMapping("/reset-password")
    public String resetPasswordView(EmailTokenParameter parameter, Model model) {
        boolean mailChecked = false;
        AccountVo accountVo = new AccountVo();

        Account account = accountService.findByEmail(parameter.getEmail());
        ResultHandler result = accountService.checkExistence(account, parameter);
        if (result.isSuccess()) {
            mailChecked = true;
            accountVo.setId(account.getId());
        }

        model.addAttribute("accountVo", accountVo);
        model.addAttribute("mailChecked", mailChecked);
        return "app/auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute("accountVo") AccountVo accountVo, Model model) {


        accountService.resetPassword(accountVo);

        return "app/auth/reset-password-result";
    }

    @PostMapping("/reset-password-mail")
    public String sendResetPasswordMail(@ModelAttribute("accountVo") AccountVo accountVo, Model model) {
        if (!accountService.existsByEmail(accountVo.getEmail())) {
            model.addAttribute("errorMessage", "해당 이메일로 가입한 계정이 없습니다.");
            return "app/auth/reset-password";
        }

        ResultHandler result = accountService.sendResetPasswordMail(accountVo);
        if (result.isSuccess()) {
            return "app/auth/mail-notification";
        }

        return "app/auth/reset-password";
    }
}
