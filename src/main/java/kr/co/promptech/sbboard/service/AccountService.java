package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.enums.AccountType;
import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.AccountAdapter;
import kr.co.promptech.sbboard.model.dto.AccountDto;
import kr.co.promptech.sbboard.model.parameter.EmailTokenParameter;
import kr.co.promptech.sbboard.repository.AccountRepository;
import kr.co.promptech.sbboard.util.ResultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
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

    public Account signUp(AccountDto accountDto) {
        String encoded = passwordEncoder.encode(accountDto.getPassword());
        accountDto.setPassword(encoded);

        Account account = modelMapper.map(accountDto, Account.class);
        String token = this.generateEmailConfirmToken();
        account.setEmailConfirmToken(token);

        account.setAuthorities(AccountType.MEMBER.authority());

        return accountRepository.save(account);
    }

    public ResultHandler sendConfirmationMail(Account account) {
        ResultHandler result = new ResultHandler();

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
            result.setFailure(true);
            result.setErrorMessage("메일 전송 에러");
            return result;
        }

        return result;
    }

    public ResultHandler checkExistence(Account account, EmailTokenParameter parameter) {
        ResultHandler result = new ResultHandler();

        if (account == null) {
            result.setFailure(true);
            result.setErrorMessage("해당 이메일을 가진 회원정보가 없습니다.");
            return result;
        }

        if (!account.getEmailConfirmToken().equals(parameter.getToken())) {
            result.setFailure(true);
            result.setErrorMessage("유효하지 않은 토큰 값입니다.");
            return result;
        }

        return result;
    }

    public void updateEmailConfirmed(Account account) {
        account.setEmailConfirmed(true);
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new UsernameNotFoundException(email);
        }

        return new AccountAdapter(account);
    }

}
