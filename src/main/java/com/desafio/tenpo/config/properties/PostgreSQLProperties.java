package com.desafio.tenpo.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.r2dbc")
public class PostgreSQLProperties {
    private String username;
    private String password;
    private String database;
    private String host;
    private int port;
}
