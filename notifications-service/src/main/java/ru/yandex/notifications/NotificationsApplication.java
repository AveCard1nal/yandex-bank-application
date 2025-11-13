package ru.yandex.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class NotificationsApplication {
    static void main(String[] args) {
        SpringApplication.run(NotificationsApplication.class, args);
    }
}
