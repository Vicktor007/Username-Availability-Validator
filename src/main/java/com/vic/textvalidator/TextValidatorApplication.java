package com.vic.textvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TextValidatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextValidatorApplication.class, args);
    }

}
