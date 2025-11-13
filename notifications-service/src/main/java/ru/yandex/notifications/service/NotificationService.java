package ru.yandex.notifications.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    @CircuitBreaker(name = "notify", fallbackMethod = "fallback")
    public void notify(String login, String message) {
        log.info("Notification for {}: {}", login, message);
    }

    public void fallback(String login, String message) {
        log.warn("Notification fallback for {}: {}", login, message);
    }
}