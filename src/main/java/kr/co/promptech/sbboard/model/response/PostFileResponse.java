package kr.co.promptech.sbboard.model.response;

import kr.co.promptech.sbboard.model.File;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostFileResponse {
    private String originalName;
    private InputStreamResource source;
}
