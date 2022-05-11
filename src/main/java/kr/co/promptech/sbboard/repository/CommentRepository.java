package kr.co.promptech.sbboard.repository;

import kr.co.promptech.sbboard.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}