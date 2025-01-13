package com.desafio.tenpo.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.desafio.tenpo.repository")
public class R2dbConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost") // Cambia al host de tu base de datos
                        .port(5432) // Puerto por defecto de PostgreSQL
                        .username("tenpo") // Usuario
                        .password("tenpo") // Contraseña
                        .database("mydb") // Nombre de la base de datos
                        .build()
        );
    }

}
