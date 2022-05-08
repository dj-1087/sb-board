package kr.co.promptech.sbboard.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultHandler {
    private boolean isFailure;
    private String errorMessage;

    public ResultHandler() {
        this.isFailure = false;
        this.errorMessage = "";
    }
}
