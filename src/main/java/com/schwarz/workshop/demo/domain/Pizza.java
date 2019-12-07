package com.schwarz.workshop.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("pizzas")
public class Pizza {

    @Id
    private Long id;

    private String name;

    private BigDecimal price;
}
