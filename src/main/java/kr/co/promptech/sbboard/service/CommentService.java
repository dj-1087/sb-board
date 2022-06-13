package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Comment;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.vo.CommentVo;
import kr.co.promptech.sbboard.repository.CommentRepository;
import kr.co.promptech.sbboard.repository.PostRepository;
import kr.co.promptech.sbboard.util.ResultHandler;
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

        Comment comment = modelMapper.map(commentVo, Comment.class);

        return commentRepository.save(comment);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public ResultHandler update(Long id, CommentVo commentVo) {
        ResultHandler result = new ResultHandler();

        Comment comment = commentRepository.findById(id).orElseThrow(null);
        if (comment == null) {
            result.setSuccess(false);
            result.setErrorMessage("해당 댓글이 없습니다.");
            return result;
        }

        comment.setContent(commentVo.getContent());
        commentRepository.save(comment);
        return result;
    }
}
