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

	@Bean
	public AuditorAware<String> auditorProvider() {
		// TODO: 추후 username으로 변경
		return () -> Optional.of("dj-1087");
	}
}
