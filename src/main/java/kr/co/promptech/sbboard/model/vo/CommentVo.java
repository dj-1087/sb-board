package kr.co.promptech.sbboard.model.vo;

import kr.co.promptech.sbboard.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo {
    private Long postId;

    private Account account;

    private String content;
}
