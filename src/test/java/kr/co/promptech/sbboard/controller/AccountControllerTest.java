package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.enums.AccountType;
import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.repository.AccountRepository;
import kr.co.promptech.sbboard.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @MockBean
    AccountService accountService;

    @Test
    @DisplayName("회원가입 화면 표시 테스트")
    void signUpView() throws Exception {
        mockMvc.perform(get("/auth/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("/app/auth/sign-up"))
                .andExpect(model().attributeExists("accountVo"));
    }

    @Test
    @DisplayName("회원가입 처리 - 정상")
    void signUp() throws Exception {
        mockMvc.perform(post("/auth/sign-up")
                        .param("nickname", "dj.jeong")
                        .param("email", "sam1087@naver.com")
                        .param("password", "ptech6441!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("sam"));

        Account account = accountRepository.findByEmail("sam1087@email.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "ptech6441!");
        assertNotNull(account.getEmailConfirmToken());
        then(accountService).should().sendConfirmationMail(any(Account.class));
    }

    @Test
    @DisplayName("회원가입 처리 - 입력값 오류")
    void signUpWithWrongValue() throws Exception {
        mockMvc.perform(post("/auth/sign-up")
                        .param("nickname", "dj.jeong")
                        .param("email", "sdfafm")
                        .param("password", "12345")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/app/auth/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증 메일 확인 - 에러")
    @Test
    void checkEmailTokenWithError() throws Exception {
        mockMvc.perform(get("/auth/email-token")
                        .param("token", "safsafdsaf")
                        .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("/app/auth/authenticate"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증 메일 확인 - 정상")
    @Test
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                .email("sam1087@naver.com")
                .password("ptech6441!")
                .nickname("dj.jeong")
                .authorities(AccountType.MEMBER.authority())
                .build();
        String token = accountService.generateEmailConfirmToken();
        account.setEmailConfirmToken(token);
        Account savedAccount = accountRepository.save(account);

        mockMvc.perform(get("/auth/email-token")
                        .param("token", savedAccount.getEmailConfirmToken())
                        .param("email", savedAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("/app/auth/authenticate"))
                .andExpect(authenticated().withUsername("dj.jeong"));
    }
}