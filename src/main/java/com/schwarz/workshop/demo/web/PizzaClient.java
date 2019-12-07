package com.schwarz.workshop.demo.web;

import com.schwarz.workshop.demo.domain.Pizza;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PizzaClient {

    private static final String API = "http://localhost:3000/pizzas";

    private final WebClient webClient = WebClient.builder().build();

    Flux<Pizza> getExternalPizzas() {
        return this.webClient.get()
                .uri(API)
                .retrieve()
                .bodyToFlux(Pizza.class);
    }

    Mono<Pizza> getExternalPizza(Long id) {
        return this.webClient.get()
                .uri(String.format("%s/%d", API, id))
                .retrieve()
                .bodyToMono(Pizza.class)
                .onErrorResume(e -> Mono.empty());
    }

}
