package kr.co.promptech.sbboard.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResultHandler {
    private boolean isSuccess;
    private String errorMessage;

    public ResultHandler() {
        this.isSuccess = false;
        this.errorMessage = "";
    }
}
