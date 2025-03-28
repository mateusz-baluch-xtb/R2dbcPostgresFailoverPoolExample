package pl.baluch.r2dbc.failoverpool;

import io.r2dbc.postgresql.MultiHostConnectionStrategy;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MIN_IDLE;
import static io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.TARGET_SERVER_TYPE;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class Main {

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {

        var connectionsToCreate = 2;

        var connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
            .option(DRIVER, "pool")
            .option(HOST, "127.0.0.1:5432,127.0.0.1:5433")
            .option(DATABASE, "test")
            .option(USER, "user")
            .option(PASSWORD, "password")
            .option(PROTOCOL, "postgresql:failover")
            .option(TARGET_SERVER_TYPE, MultiHostConnectionStrategy.TargetServerType.PREFER_SECONDARY)
            .option(MIN_IDLE, connectionsToCreate)
            .option(MAX_SIZE, connectionsToCreate)
            .build());

        Flux.range(0, connectionsToCreate)
            .flatMap(i -> connectionFactory.create())
            .flatMap(this::isPrimary)
            .map(isPrimary -> Boolean.TRUE.equals(isPrimary) ? "primary" : "standby")
            .doOnNext(serverType -> System.out.println("Connected to " + serverType))
            .blockLast();
    }

    private Mono<Boolean> isPrimary(Connection connection) {
        return Mono.from(connection.createStatement("SHOW TRANSACTION_READ_ONLY").execute())
            .flatMap(result -> Mono.from(result.map((row, meta) -> row.get(0, String.class))))
            .map(str -> str.equalsIgnoreCase("off"));
    }
}