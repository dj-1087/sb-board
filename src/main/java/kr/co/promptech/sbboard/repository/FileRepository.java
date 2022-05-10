package kr.co.promptech.sbboard.repository;

import kr.co.promptech.sbboard.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}