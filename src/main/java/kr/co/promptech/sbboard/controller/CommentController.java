package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Comment;
import kr.co.promptech.sbboard.model.dto.CommentDto;
import kr.co.promptech.sbboard.model.dto.PostDto;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.vo.CommentVo;
import kr.co.promptech.sbboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final ModelMapper modelMapper;

    @PostMapping("")
    public ResponseEntity<?> create(@CurrentUser Account account, @RequestBody CommentVo commentVo, Model model) {

        log.info("=============comment controller=============");
        log.info(commentVo.getContent());
        log.info(commentVo.getPostId().toString());

        commentVo.setAccount(account);
        Comment comment = commentService.save(commentVo);
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        commentDto.setNickname(comment.getAccount().getNickname());

        log.info("=============check datetime=============");
        if (comment.getCreatedAt() == null) {
            log.info("no datetime");
            log.info("id: " + comment.getId().toString());
        }


        return ResponseEntity.ok().body(commentDto);
    }

}
