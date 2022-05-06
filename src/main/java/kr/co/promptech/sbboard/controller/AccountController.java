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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountValidator accountValidator;

    private final ModelMapper modelMapper;

    private final JavaMailSender mailSender;

    @Value("${app.url}")
    private String APPLICATION_URL;

    @Value("${spring.mail.username}")
    private String EMAIL_SENDER_ADDRESS;

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

        MimeMessage mailMessage = mailSender.createMimeMessage();
        try {
            mailMessage.setSubject("[Spring Boot 게시판] 회원가입 메일 인증");
            String link = APPLICATION_URL + "/auth/email-token?email=" + account.getEmail() +
                    "&token=" + account.getEmailConfirmToken();
            String mailContent = "<h3>아래 링크를 통해 메일 인증을 완료해주세요</h3>\n" +
                    "<a href='" + link + "'>" + link + "</a>";
            mailMessage.setText(mailContent, "UTF-8", "html");

            mailMessage.setFrom(EMAIL_SENDER_ADDRESS);
            mailMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(account.getEmail(), account.getNickname(), "UTF-8"));

            mailSender.send(mailMessage);
        } catch (MessagingException | UnsupportedEncodingException exception) {
            model.addAttribute("error", "메일 전송 에러");
            return "/app/auth/sign-up";
        }

        return "redirect:/";
    }

    @GetMapping("/email-token")
    public String checkEmailToken(String email, String token, Model model) {
        Account account = accountService.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "해당 이메일을 가진 회원정보가 없습니다.");
            return "/app/auth/authenticate";
        }

        if (!account.getEmailConfirmToken().equals(token)) {
            model.addAttribute("error", "유효하지 않은 토큰 값입니다.");
            return "/app/auth/authenticate";
        }

        account.setEmailConfirmed(true);
        accountService.save(account);
        model.addAttribute("error", null);
        model.addAttribute("nickname", account.getNickname());
        return "/app/auth/authenticate";
    }
}
