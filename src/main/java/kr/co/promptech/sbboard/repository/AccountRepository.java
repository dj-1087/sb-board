package kr.co.promptech.sbboard.repository;

import kr.co.promptech.sbboard.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Account findByEmail(String email);

    Account findByNickname(String nickname);
}