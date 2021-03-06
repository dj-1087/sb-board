package kr.co.promptech.sbboard.model;

import kr.co.promptech.sbboard.model.base.BaseTimeEntity;
import kr.co.promptech.sbboard.model.enums.AccountType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    List<Thumb> thumbs = new ArrayList<>();

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailConfirmed;

    private String emailConfirmToken;

    private String authorities;

    public boolean isAdmin() {
        for (String role : this.getAuthorities().split(",")) {
            if (role.equals(AccountType.ADMIN.authority())) {
                return true;
            }
        }
        return false;
    }
}
