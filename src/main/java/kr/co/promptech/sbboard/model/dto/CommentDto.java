package kr.co.promptech.sbboard.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
public class CommentDto {
    private Long id;
    private String accountNickname;
    private Long accountId;
    private String content;
    private Instant createdAt;
}
