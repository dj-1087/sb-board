package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Comment;
import kr.co.promptech.sbboard.model.dto.CommentDto;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.vo.CommentVo;
import kr.co.promptech.sbboard.service.CommentService;
import kr.co.promptech.sbboard.util.ResultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<?> index(@RequestParam Long postId) {
        List<Comment> commentList = commentService.findAllByPostId(postId);
        List<CommentDto> commentDtoList = modelMapper.map(commentList,
                new TypeToken<List<CommentDto>>() {
                }.getType());

        return ResponseEntity.ok().body(commentDtoList);
    }

    @PostMapping("")
    public ResponseEntity<?> create(@CurrentUser Account account, @RequestBody CommentVo commentVo) {
        log.info("=============comment controller=============");
        log.info(commentVo.getContent());
        log.info(commentVo.getPostId().toString());

        commentVo.setAccount(account);
        Comment comment = commentService.save(commentVo);
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);

        log.info("=============check datetime=============");
        if (comment.getCreatedAt() == null) {
            log.info("no datetime");
            log.info("id: " + comment.getId().toString());
        }


        return ResponseEntity.ok().body(commentDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") Long id, @RequestBody CommentVo commentVo) {
        ResultHandler result = commentService.update(id, commentVo);
        if (result.isFailure()) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }

}
