package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.AccountAdapter;
import kr.co.promptech.sbboard.model.enums.AccountType;
import kr.co.promptech.sbboard.model.parameter.EmailTokenParameter;
import kr.co.promptech.sbboard.model.vo.AccountVo;
import kr.co.promptech.sbboard.repository.AccountRepository;
import kr.co.promptech.sbboard.util.ResultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.url}")
    private String APPLICATION_URL;

    @Value("${spring.mail.username}")
    private String EMAIL_SENDER_ADDRESS;

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public String generateEmailConfirmToken() {
        return UUID.randomUUID().toString();
    }

    public Account signUp(AccountVo accountVo) {
        String encoded = passwordEncoder.encode(accountVo.getPassword());
        accountVo.setPassword(encoded);

        Account account = modelMapper.map(accountVo, Account.class);
        String token = this.generateEmailConfirmToken();
        account.setEmailConfirmToken(token);

        account.setAuthorities(AccountType.MEMBER.authority());

        return accountRepository.save(account);
    }

    public ResultHandler sendConfirmationMail(Account account) {
        ResultHandler result = new ResultHandler();

        MimeMessage mailMessage = mailSender.createMimeMessage();
        try {
            mailMessage.setSubject("[Spring Boot ?????????] ???????????? ?????? ??????");
            String link = APPLICATION_URL + "/auth/email-token?email=" + account.getEmail() +
                    "&token=" + account.getEmailConfirmToken();
            String mailContent = "<h3>?????? ????????? ?????? ?????? ????????? ??????????????????</h3>\n" +
                    "<a href='" + link + "'>" + link + "</a>";
            mailMessage.setText(mailContent, "UTF-8", "html");

            mailMessage.setFrom(EMAIL_SENDER_ADDRESS);
            mailMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(account.getEmail(), account.getNickname(), "UTF-8"));

            mailSender.send(mailMessage);
            result.setSuccess(true);

        } catch (MessagingException | UnsupportedEncodingException exception) {
            result.setSuccess(false);
            result.setErrorMessage("?????? ?????? ??????");
            return result;
        }

        return result;
    }

    public ResultHandler checkExistence(Account account, EmailTokenParameter parameter) {
        ResultHandler result = new ResultHandler();

        if (account == null) {
            result.setSuccess(false);
            result.setErrorMessage("?????? ???????????? ?????? ??????????????? ????????????.");
            return result;
        }

        if (!account.getEmailConfirmToken().equals(parameter.getToken())) {
            result.setSuccess(false);
            result.setErrorMessage("???????????? ?????? ?????? ????????????.");
            return result;
        }

        result.setSuccess(true);
        return result;
    }

    public void updateEmailConfirmed(Account account) {
        account.setEmailConfirmed(true);
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, LockedException {
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new UsernameNotFoundException(email);
        }
        if (!account.isEmailConfirmed()) {
            return new AccountAdapter(account, true, true, true, false);
        }

        return new AccountAdapter(account);
    }

    public Account resetEmailConfirmToken(Account account) {
        String token = this.generateEmailConfirmToken();
        account.setEmailConfirmToken(token);
        return accountRepository.save(account);
    }

    public String findEmailByNickname(String nickname) {
        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            return null;
        }

        return account.getEmail();
    }

    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    public ResultHandler sendResetPasswordMail(AccountVo accountVo) {
        Account account = accountRepository.findByEmail(accountVo.getEmail());

        account = this.resetEmailConfirmToken(account);

        ResultHandler result = new ResultHandler();

        MimeMessage mailMessage = mailSender.createMimeMessage();
        try {
            mailMessage.setSubject("[Spring Boot ?????????] ???????????? ????????? ??????");
            String link = APPLICATION_URL + "/auth/reset-password?email=" + account.getEmail() +
                    "&token=" + account.getEmailConfirmToken();
            String mailContent = "<h3>?????? ????????? ?????? ??????????????? ?????? ??????????????????.</h3>\n" +
                    "<a href='" + link + "'>" + link + "</a>";
            mailMessage.setText(mailContent, "UTF-8", "html");

            mailMessage.setFrom(EMAIL_SENDER_ADDRESS);
            mailMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(account.getEmail(), account.getNickname(), "UTF-8"));

            mailSender.send(mailMessage);
            result.setSuccess(true);

        } catch (MessagingException | UnsupportedEncodingException exception) {
            result.setSuccess(false);
            result.setErrorMessage("?????? ?????? ??????");
            return result;
        }

        return result;
    }

    public void resetPassword(AccountVo accountVo) {
        Account account = accountRepository.findById(accountVo.getId()).orElse(null);
        if (account == null) {
            log.info("=======resetPassword() > account null=======");
            return;
        }

        String encoded = passwordEncoder.encode(accountVo.getPassword());
        account.setPassword(encoded);

        accountRepository.save(account);
    }
}
