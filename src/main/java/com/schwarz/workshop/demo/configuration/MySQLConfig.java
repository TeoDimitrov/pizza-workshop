package com.schwarz.workshop.demo.configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class MySQLConfig {

    @Bean
    public ConnectionFactory getMySqlConnectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, "127.0.0.1")
                .option(USER, "root")
                .option(PORT, 3306)
                .option(PASSWORD, "12345678")
                .option(DATABASE, "r2dbc")
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(30))
                .build());

    }
}
