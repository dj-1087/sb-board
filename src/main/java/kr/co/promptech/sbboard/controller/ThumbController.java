package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.dto.PostThumbDto;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.service.PostService;
import kr.co.promptech.sbboard.service.ThumbService;
import kr.co.promptech.sbboard.util.ResultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/thumbs")
@RequiredArgsConstructor
public class ThumbController {

    private final ThumbService thumbService;
    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<?> thumbsUp(@CurrentUser Account account, @PathVariable("postId") Long postId) {
        Post post = postService.findById(postId);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        if (thumbService.checkUserClicked(post, account)) {
            String errorMessage = "이미 해당 게시물에 대해 공감을 눌렀습니다.";
            ResultHandler result = ResultHandler.builder()
                    .isSuccess(false)
                    .errorMessage(errorMessage)
                    .build();
            return ResponseEntity.badRequest().body(result);
        }

        thumbService.thumbsUp(account, post);
        PostThumbDto postThumbDto = thumbService.getThumbDtoByPost(post, account);

        return ResponseEntity.ok().body(postThumbDto);
    }
}
