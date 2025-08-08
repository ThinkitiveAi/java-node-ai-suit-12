package com.healthfirst.healthfirstserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HealthFirstApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthFirstApplication.class, args);
    }
}
