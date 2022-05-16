package kr.co.promptech.sbboard.model.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.InputStreamResource;

@Getter
@Setter
public class PostFileResponse {
    private String originalName;
    private InputStreamResource source;
}
