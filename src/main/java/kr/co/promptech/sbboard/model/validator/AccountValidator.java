package kr.co.promptech.sbboard.model.validator;

import kr.co.promptech.sbboard.model.dto.AccountDto;
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
        return clazz.isAssignableFrom(AccountDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountDto accountDto = (AccountDto) target;
        if(accountRepository.existsByNickname(accountDto.getNickname())){
            errors.rejectValue("nickname", "invalid.Nickname", new Object[]{accountDto.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
        if(accountRepository.existsByEmail(accountDto.getEmail())){
            errors.rejectValue("email", "invalid.Email", new Object[]{accountDto.getEmail()}, "이미 사용중인 이메일입니다.");
        }
    }
}
