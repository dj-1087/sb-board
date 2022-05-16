package kr.co.promptech.sbboard.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
public class CommentDto {
    private String id;
    private String accountNickname;
    private String content;
    private Instant createdAt;
}
