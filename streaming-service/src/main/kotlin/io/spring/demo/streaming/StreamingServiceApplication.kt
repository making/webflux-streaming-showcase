package io.spring.demo.streaming;

import com.samskivert.mustache.Mustache
import io.spring.demo.streaming.portfolio.User
import io.spring.demo.streaming.portfolio.UserRepository
import io.spring.demo.streaming.support.MustacheResourceTemplateLoader
import io.spring.demo.streaming.support.MustacheViewResolver

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mustache.MustacheProperties

import org.springframework.context.annotation.Bean
import java.time.Duration.*

@SpringBootApplication
class StreamingServiceApplication {

	@Bean
	fun mustacheViewResolver(properties: MustacheProperties) = MustacheViewResolver().apply {
		setPrefix(properties.prefix)
		setSuffix(properties.suffix)
		val loader = MustacheResourceTemplateLoader(properties.prefix, properties.suffix)
		setCompiler(Mustache.compiler().escapeHTML(false).withLoader(loader))
	}

	@Bean
	fun createUsers(userRepository: UserRepository) = CommandLineRunner {
		val users = listOf(
				User("sdeleuze", "Sebastien Deleuze"),
				User("snicoll", "Stephane Nicoll"),
				User("rstoyanchev", "Rossen Stoyanchev"),
				User("smaldini", "Stephane Maldini"),
				User("sbasle", "Simon Basle"),
				User("bclozel", "Brian Clozel")
		)
		userRepository.save(users).blockLast(ofSeconds(3))
	}

}

fun main(args: Array<String>) {
	SpringApplication.run(StreamingServiceApplication::class.java, *args)
}
