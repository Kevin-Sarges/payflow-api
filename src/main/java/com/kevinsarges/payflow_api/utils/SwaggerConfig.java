package com.kevinsarges.payflow_api.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("Payflow API")
                        .version("1.0")
                        .description("Api feita para o teste técnico da SEFA - FADESP")
        );
    }
}
