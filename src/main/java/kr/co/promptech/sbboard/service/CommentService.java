package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Comment;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.vo.CommentVo;
import kr.co.promptech.sbboard.repository.CommentRepository;
import kr.co.promptech.sbboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    public List<Comment> findAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public Comment save(CommentVo commentVo) {
//        Post post = postRepository.getById(commentVo.getPostId());

        log.info("before mapper");
        Comment comment = modelMapper.map(commentVo, Comment.class);
        log.info("after mapper");
//        comment.setPost(post);
        log.info(comment.getPost().getId().toString());
        log.info("after set post");

        Comment savedComment = commentRepository.save(comment);

        log.info("=============check datetime in service=============");
        if (savedComment.getCreatedAt() == null) {
            log.info("no datetime");
            log.info("id: " + savedComment.getCreatedAt());
            log.info("id: " + savedComment.getUpdatedAt());
        }

        return savedComment;
    }
}
