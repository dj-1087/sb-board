package kr.co.promptech.sbboard.repository;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.Thumb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbRepository extends JpaRepository<Thumb, Long> {
    int countAllByPost(Post post);

    boolean existsByPostAndAccount(Post post, Account account);
}