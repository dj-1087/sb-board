package kr.co.promptech.sbboard.repository;

import kr.co.promptech.sbboard.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
}