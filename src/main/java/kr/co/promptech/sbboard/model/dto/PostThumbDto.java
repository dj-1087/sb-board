package kr.co.promptech.sbboard.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostThumbDto {
    private int postThumbCount;
    private boolean userClicked;
}
