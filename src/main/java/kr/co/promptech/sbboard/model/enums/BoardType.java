package kr.co.promptech.sbboard.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardType {
    NOTICE("공지사항", "NOTICE"),
    QNA("Q&A", "QNA"),
    FREE("자유게시판", "FREE");

    private final String title, key;

}
