package com.schwarz.workshop.demo.repository;

import com.schwarz.workshop.demo.domain.Pizza;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaRepository extends ReactiveCrudRepository<Pizza, Long> {
}
