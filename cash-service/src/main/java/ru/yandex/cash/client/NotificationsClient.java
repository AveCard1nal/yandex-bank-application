package ru.yandex.cash.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gateway")
public interface NotificationsClient {

    @PostMapping("/api/notifications")
    void notify(@RequestBody NotificationRequest request);

    record NotificationRequest(String login, String message) {
    }
}