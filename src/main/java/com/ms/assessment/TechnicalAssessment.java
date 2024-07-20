package com.ms.assessment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition
@SpringBootApplication
public class TechnicalAssessment {
    public static void main(String[] args) {
        SpringApplication.run(TechnicalAssessment.class, args);
    }
}
