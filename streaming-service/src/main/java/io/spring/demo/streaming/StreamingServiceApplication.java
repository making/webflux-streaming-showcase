package io.spring.demo.streaming;

import java.time.Duration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.spring.demo.streaming.portfolio.User;
import io.spring.demo.streaming.portfolio.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class StreamingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamingServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner createUsers(UserRepository userRepository) {
		return strings -> {
			Flux<User> users = Flux.just(new User("sdeleuze", "Sebastien Deleuze"),
					new User("snicoll", "Stephane Nicoll"),
					new User("rstoyanchev", "Rossen Stoyanchev"),
					new User("smaldini", "Stephane Maldini"),
					new User("simonbasle", "Simon Basle"),
					new User("bclozel", "Brian Clozel"));

			users.map(u -> userRepository.findUserByGithub(u.getGithub())
					.switchIfEmpty(Mono.defer(() -> userRepository.save(u))).subscribe())
					.blockLast(Duration.ofSeconds(3));
		};
	}

}
