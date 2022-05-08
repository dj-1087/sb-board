package kr.co.promptech.sbboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@SpringBootApplication
public class SbBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbBoardApplication.class, args);
	}
}
