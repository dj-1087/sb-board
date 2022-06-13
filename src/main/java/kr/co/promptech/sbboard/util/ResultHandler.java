package kr.co.promptech.sbboard.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultHandler {
    private boolean isSuccess;
    private String errorMessage;

    public ResultHandler() {
        this.isSuccess = true;
        this.errorMessage = "";
    }
}
