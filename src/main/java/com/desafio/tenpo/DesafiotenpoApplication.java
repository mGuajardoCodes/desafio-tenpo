package com.desafio.tenpo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class DesafiotenpoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafiotenpoApplication.class, args);
	}


}
