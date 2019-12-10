package com.schwarz.workshop.demo.web;

import com.schwarz.workshop.demo.domain.Pizza;
import com.schwarz.workshop.demo.repository.PizzaRepository;
import com.schwarz.workshop.demo.web.error.InvalidParamException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.UnaryOperator;

@Component
@Transactional
@RequiredArgsConstructor
public class PizzaHandler {

    private final PizzaRepository pizzaRepository;

    private final PizzaClient pizzaClient;

    private final UnaryOperator<BigDecimal> vat = bigDecimal -> bigDecimal.multiply(BigDecimal.valueOf(1.2));

    Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Flux<Pizza> pizzaFlux = this.pizzaRepository.findAll()
                .mergeWith(this.pizzaClient.getExternalPizzas())
                .map(pizza -> {
                    pizza.setPrice(vat.apply(pizza.getPrice()));
                    return pizza;
                });

        return ServerResponse
                .ok()
                .body(pizzaFlux, Pizza.class);
    }

    Mono<ServerResponse> getById(ServerRequest serverRequest) {
        Long pizzaId;
        try {
            pizzaId = Long.valueOf(serverRequest.pathVariable("id"));
        } catch (NumberFormatException e) {
//            throw new InvalidParamException(HttpStatus.BAD_REQUEST, "Wrong param.");
            return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Wrong param.");
        }

        return this.pizzaRepository.findById(pizzaId)
                .switchIfEmpty(this.pizzaClient.getExternalPizza(pizzaId))
                .map(pizza -> {
                    pizza.setPrice(vat.apply(pizza.getPrice()));
                    return pizza;
                })
                .flatMap(pizza -> ServerResponse.ok().body(Mono.just(pizza), Pizza.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    Mono<ServerResponse> save(ServerRequest serverRequest) {
        Mono<Pizza> pizzaMono = serverRequest.bodyToMono(Pizza.class);

        return pizzaMono.flatMap(pizza -> ServerResponse
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.pizzaRepository.save(pizza), Pizza.class));
    }

    Mono<ServerResponse> update(ServerRequest serverRequest) {
        Long pizzaId = Long.valueOf(serverRequest.pathVariable("id"));
        Mono<Pizza> pizzaMono = serverRequest.bodyToMono(Pizza.class);

        Mono<Pizza> existingPizza = this.pizzaRepository.findById(pizzaId);

        return pizzaMono.zipWith(existingPizza, (newPizza, oldPizza) ->
                new Pizza(oldPizza.getId(),
                        newPizza.getName(),
                        newPizza.getPrice()))
                .flatMap(pizza -> {
                    Mono<Pizza> updatedPizza = this.pizzaRepository.save(new Pizza(pizza.getId(), pizza.getName(), pizza.getPrice()));
                    return ServerResponse.ok().body(updatedPizza, Pizza.class);
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    Mono<ServerResponse> deleteById(ServerRequest serverRequest) {
        Long pizzaId = Long.valueOf(serverRequest.pathVariable("id"));
        return this.pizzaRepository.findById(pizzaId)
                .flatMap(pizza -> ServerResponse.ok().body(this.pizzaRepository.deleteById(pizzaId), Pizza.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    Mono<ServerResponse> stream(ServerRequest serverRequest) {
        Flux<Pizza> pizzaFlux = this.pizzaRepository.findAll()
                .mergeWith(this.pizzaClient.getExternalPizzas())
                .map(pizza -> {
                    pizza.setPrice(vat.apply(pizza.getPrice()));
                    return pizza;
                })
                .delayElements(Duration.ofSeconds(1));

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(pizzaFlux, Pizza.class);
    }
}
