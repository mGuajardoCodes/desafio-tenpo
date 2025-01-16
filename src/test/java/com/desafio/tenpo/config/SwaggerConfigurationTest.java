package com.desafio.tenpo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@Configuration
public class SwaggerConfigurationTest {

    @Test
    void testCustomOpenAPI() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SwaggerConfiguration.class);

        OpenAPI openAPI = context.getBean(OpenAPI.class);

        assertThat(openAPI).isNotNull();
        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Desafío Tenpo API");
        assertThat(info.getVersion()).isEqualTo("1.0");
        assertThat(info.getDescription()).isEqualTo("Documentación de la API para el Desafío Tenpo");

        context.close();
    }
}
