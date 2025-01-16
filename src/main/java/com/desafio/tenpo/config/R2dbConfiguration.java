package com.desafio.tenpo.config;

import com.desafio.tenpo.config.properties.PostgreSQLProperties;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@AllArgsConstructor
@EnableR2dbcRepositories(basePackages = "com.desafio.tenpo.repository")
public class R2dbConfiguration {

    private PostgreSQLProperties config;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .port(config.getPort())
                        .username(config.getUsername())
                        .password(config.getPassword())
                        .database(config.getDatabase())
                        .host(config.getHost())
                        .build()
        );
    }

}
