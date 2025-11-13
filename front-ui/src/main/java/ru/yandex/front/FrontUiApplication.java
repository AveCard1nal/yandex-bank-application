package ru.yandex.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FrontUiApplication {
    static void main(String[] args) {
        SpringApplication.run(FrontUiApplication.class, args);
    }
}