package kr.co.promptech.sbboard.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class AccountAdapter extends User {
    private final Account account;

    public AccountAdapter(Account account) {
        super(account.getNickname(), account.getPassword(), authorities(account));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(Account account)  {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (String role : account.getAuthorities().split(",")) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return roles;
    }
}
