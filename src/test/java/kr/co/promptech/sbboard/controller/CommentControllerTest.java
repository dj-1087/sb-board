package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Comment;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.enums.BoardType;
import kr.co.promptech.sbboard.model.vo.CommentVo;
import kr.co.promptech.sbboard.repository.AccountRepository;
import kr.co.promptech.sbboard.repository.CommentRepository;
import kr.co.promptech.sbboard.repository.PostRepository;
import kr.co.promptech.sbboard.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class CommentControllerTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    PasswordEncoder encoder;

    @Test
    @DisplayName("댓글 생성 - BaseEntity 체크")
    void checkBaseEntity() {

        String password = encoder.encode("password");
        Account account = Account.builder()
                .email("example@email.com")
                .nickname("nickname")
                .password(password)
                .emailConfirmed(true)
                .emailConfirmToken(UUID.randomUUID().toString())
                .build();
        Account savedAccount = accountRepository.save(account);

        Post post = Post.builder()
                .account(savedAccount)
                .title("title")
                .content("content")
                .boardType(BoardType.FREE)
                .build();
        Post savedPost = postRepository.save(post);


        Comment comment = Comment.builder()
                .account(savedAccount)
                .post(savedPost)
                .content("test comment")
                .build();
        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment.getCreatedAt()).isNotNull();
        assertThat(savedComment.getUpdatedAt()).isNotNull();

        log.info("=====================================");
        log.info(savedComment.getCreatedAt().toString());
    }

    @Test
    @DisplayName("댓글 생성 - BaseEntity 체크 with CommentService")
    void checkBaseEntityWithCommentService() {

        // given
        String password = encoder.encode("password");
        Account account = Account.builder()
                .email("example@email.com")
                .nickname("nickname")
                .password(password)
                .emailConfirmed(true)
                .emailConfirmToken(UUID.randomUUID().toString())
                .build();
        Account savedAccount = accountRepository.save(account);

        Post post = Post.builder()
                .account(savedAccount)
                .title("title")
                .content("content")
                .boardType(BoardType.FREE)
                .build();
        Post savedPost = postRepository.save(post);

        CommentVo commentVo = new CommentVo(savedPost.getId(), null, "test comment");

        // when
        commentVo.setAccount(savedAccount);
        Comment savedComment = commentService.save(commentVo);

        // then
        assertThat(savedComment.getCreatedAt()).isNotNull();
        assertThat(savedComment.getUpdatedAt()).isNotNull();

        log.info("=====================================");
        log.info(savedComment.getCreatedAt().toString());
    }
}