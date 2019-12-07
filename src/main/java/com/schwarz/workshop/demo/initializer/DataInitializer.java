package com.schwarz.workshop.demo.initializer;

import com.schwarz.workshop.demo.repository.PizzaRepository;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS pizzas (" +
            "id bigint auto_increment primary key ," +
            "name varchar(255)," +
            "price double" +
            ")";

    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE pizzas";

    private static final String INSERT_NAPOLI = "INSERT INTO pizzas(name, price) VALUES('Napoli', 8.40)";
    private static final String INSERT_CARBONARA = "INSERT INTO pizzas(name, price) VALUES('Carbonara', 9.10)";
    private static final String INSERT_MARGHERITA = "INSERT INTO pizzas(name, price) VALUES('Margherita', 7.45)";

    private final ConnectionFactory connectionFactory;

    private final PizzaRepository pizzaRepository;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.debug("Populating data..");

        this.createAndInsert();

        LOGGER.debug("Data loaded..");
    }

    /**
     * Option 1. Insert with connection.
     */
    private void createAndInsert() {
        Mono.from(connectionFactory.create())
                // Create Table
                .flatMapMany(c -> c.createStatement(CREATE_TABLE).execute())

                // Truncate Table
                .then(Mono.from(connectionFactory.create()))
                .flatMapMany(c -> c.createStatement(TRUNCATE_TABLE).execute())

                // Insert
                .then(Mono.from(connectionFactory.create()))
                .flatMap(h2Connection -> Mono.from(h2Connection.beginTransaction())
                        .then(Mono.from(h2Connection
                                .createBatch()
                                .add(INSERT_NAPOLI)
                                .add(INSERT_CARBONARA)
                                .add(INSERT_MARGHERITA)
                                .execute())
                        )
                        // Wait for the transaction
                        .delayUntil(res -> h2Connection.commitTransaction())
                        // Commit
                        .doFinally(st -> h2Connection.close())
                )
                // Wait for the data to start
                .block();
    }

}
