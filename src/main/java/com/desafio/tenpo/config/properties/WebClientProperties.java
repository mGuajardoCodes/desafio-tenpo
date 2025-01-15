package com.desafio.tenpo.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "webclient")
public class WebClientProperties {
    private int serviceCallMaxRetries;
    private long timeoutInMs;
}
