package com.exmaple.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@Slf4j
@EnableKafka
@SpringBootApplication
public class SenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SenderApplication.class, args);
    }
}
