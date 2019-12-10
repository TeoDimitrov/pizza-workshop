package com.schwarz.workshop.demo.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootTest
public class PizzaHandlerTest {

    @Autowired
    private RouterFunction<ServerResponse> routerFunction;

    @Test
    public void findAll() {

        WebTestClient webTestClient = WebTestClient.bindToRouterFunction(this.routerFunction).build();

        webTestClient.get().uri("/api/pizzas/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Napoli")
                .jsonPath("$.price").isEqualTo(10.08);

    }
}
