package kr.co.promptech.sbboard.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    ADMIN("관리자"),
    MEMBER("회원");

    private final String title;

    public String authority() {
        return "ROLE_" + this.name();
    }

}
