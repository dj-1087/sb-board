package kr.co.promptech.sbboard.model.vo;

import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.enums.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostVo {
    private Long id;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private BoardType boardType;

    private List<MultipartFile> files;

    private Set<File> fileSet;
}
