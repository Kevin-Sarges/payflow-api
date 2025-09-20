package com.kevinsarges.payflow_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayflowApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayflowApiApplication.class, args);
		System.out.println("\uD83D\uDE80 Aplicação iniciada com sucesso e pronta para uso!");
		System.out.println("Swagger - http://localhost:8080/swagger-ui/index.html");
		System.out.println("H2 - http://localhost:8080/h2-console");
	}

}
