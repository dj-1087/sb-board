package kr.co.promptech.sbboard.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardType {
    NOTICE("공지사항"),
    QNA("Q&A"),
    FREE("자유게시판");

    private final String title;

}
