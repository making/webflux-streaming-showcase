package io.spring.demo.streaming.portfolio;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import io.spring.demo.streaming.StreamingServiceConfigProps;
import reactor.core.publisher.Flux;

@Controller
public class HomeController {

	private final UserRepository userRepository;
	private final StreamingServiceConfigProps configProps;

	public HomeController(UserRepository userRepository,
			StreamingServiceConfigProps configProps) {
		this.userRepository = userRepository;
		this.configProps = configProps;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("users", this.userRepository.findAll());
		return "index";
	}

	@GetMapping(path = "/quotes/feed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<Quote> fetchQuotesStream() {
		return WebClient.create(configProps.getQuoteServiceUrl())
				.get()
				.uri("/quotes")
				.accept(APPLICATION_STREAM_JSON)
				.retrieve()
				.bodyToFlux(Quote.class)
				.share()
				.log();
	}

	@GetMapping("/quotes")
	public String quotes() {
		return "quotes";
	}
}
