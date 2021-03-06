package kr.co.promptech.sbboard.repository;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    boolean existsByIdAndAccount(Long id, Account account);
}