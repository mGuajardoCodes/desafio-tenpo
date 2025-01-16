package com.desafio.tenpo.config;

import com.desafio.tenpo.config.properties.RedisProperties;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@AllArgsConstructor
public class RedisConfiguration {

    private RedisProperties config;

    public static final String CACHE_NAME = "percentageCache";
    public static final String KEY_OF_PERCENTAGE = "callToGetPercentage";
    private static final Logger log = LoggerFactory.getLogger(RedisConfiguration.class);


    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        log.info("Connecting to redis, port {} and host {}", config.getPort(), config.getHost());
        return new LettuceConnectionFactory(config.getHost(), config.getPort());
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

        // Configure serializers for keys and values
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> context = builder
                .value(new GenericJackson2JsonRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }
}
