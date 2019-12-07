package com.schwarz.workshop.demo.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PizzaRouter {

    @Bean
    public RouterFunction<ServerResponse> getPizzaRoutes(PizzaHandler pizzaHandler) {

        return nest(path("/api/pizzas").and(accept(MediaType.APPLICATION_JSON)),
                route(GET("/"), pizzaHandler::getAll)
                        .andRoute(GET("/stream").and(accept(MediaType.TEXT_EVENT_STREAM)), pizzaHandler::stream)
                        .andRoute(GET("/{id}"), pizzaHandler::getById)
                        .andRoute(POST("/"), pizzaHandler::save)
                        .andRoute(PUT("/{id}"), pizzaHandler::update)
                        .andRoute(DELETE("/{id}"), pizzaHandler::deleteById)
        );
    }
}
