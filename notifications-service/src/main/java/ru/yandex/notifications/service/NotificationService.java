package ru.yandex.notifications.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    private final MeterRegistry meterRegistry;

    public NotificationService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @CircuitBreaker(name = "notify", fallbackMethod = "fallback")
    public void notify(String login, String message) {
        log.info("Sending notification for {}: {}", login, message);
        log.debug("Notification content for {}: {}", login, message);
    }

    public void fallback(String login, String message, Throwable ex) {
        log.error("Failed to send notification for {}. Error: {}", login, ex.getMessage());
        Counter.builder("notification.failure")
                .tag("login", login)
                .register(meterRegistry)
                .increment();
        log.warn("Notification fallback for {}: {}", login, message);
    }
}