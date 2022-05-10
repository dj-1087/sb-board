package kr.co.promptech.sbboard.model.validator;

import kr.co.promptech.sbboard.model.vo.AccountVo;
import kr.co.promptech.sbboard.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class AccountValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccountVo.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountVo accountVo = (AccountVo) target;
        if(accountRepository.existsByNickname(accountVo.getNickname())){
            errors.rejectValue("nickname", "invalid.Nickname", new Object[]{accountVo.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
        if(accountRepository.existsByEmail(accountVo.getEmail())){
            errors.rejectValue("email", "invalid.Email", new Object[]{accountVo.getEmail()}, "이미 사용중인 이메일입니다.");
        }
    }
}
