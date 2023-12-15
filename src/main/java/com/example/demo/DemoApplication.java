package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class DemoApplication {

	static Logger log = LoggerFactory.getLogger(DemoApplication.class);

	@Autowired
	BeanFactory beanFactory;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		Hooks.enableAutomaticContextPropagation();
	}

	@Bean
	WebClient webClient(WebClient.Builder clientBuilder) {
		return clientBuilder
				.baseUrl("http://localhost:8080")
				.build();
	}

	@GetMapping(value = "/test")
	public Mono<DTO> test() {
		log.info("call external service");
		return beanFactory.getBean(WebClient.class)
				.get()
				.uri("/external-service")
				.exchangeToMono(clientResponse -> DataBufferUtils.join(
						clientResponse.body(BodyExtractors.toDataBuffers())
								.doOnNext(b -> log.info("streaming response"))))
				.doOnNext(a -> log.info("response collected"))
				.map(Object::toString)
				.map(DTO::new);
	}

	@GetMapping(value = "/external-service")
	public Flux<DTO> externalService() {
		log.info("external service called");
		return Flux.just(new DTO("test")).repeat(5);
	}

	public record DTO(String a) {
	}

}
