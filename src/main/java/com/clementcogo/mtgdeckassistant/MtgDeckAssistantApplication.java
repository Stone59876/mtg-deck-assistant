package com.clementcogo.mtgdeckassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MtgDeckAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtgDeckAssistantApplication.class, args);
    }

}
