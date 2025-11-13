package ru.yandex.notifications.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.notifications.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    public record NotificationRequest(String login, String message) {
    }

    @PostMapping
    public void notify(@RequestBody NotificationRequest request) {
        service.notify(request.login(), request.message());
    }
}