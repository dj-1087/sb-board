package kr.co.promptech.sbboard.model.dto;

import kr.co.promptech.sbboard.model.Account;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
public class CommentDto {
    private String id;
    private String nickname;
    private String content;
    private Instant createdAt;
}
