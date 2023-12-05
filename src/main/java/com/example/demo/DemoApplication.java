package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class DemoApplication {

	static Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		Hooks.enableAutomaticContextPropagation();
	}

	@GetMapping(value = "/mono")
	public Mono<Void> mono() {
		log.info("Should log with traceId and spanId");
		return Mono.empty();
	}
}
