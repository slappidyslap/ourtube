package io.melakuera.ourtube;

import io.melakuera.ourtube.repo.CommentRepo;
import io.melakuera.ourtube.repo.UserRepo;
import io.melakuera.ourtube.repo.VideoRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OurtubeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OurtubeApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(CommentRepo commentRepo, VideoRepo videoRepo, UserRepo userRepo) {
		return (args) -> {
			commentRepo.deleteAll();
			videoRepo.deleteAll();
			userRepo.deleteAll();
		};
	}

}
