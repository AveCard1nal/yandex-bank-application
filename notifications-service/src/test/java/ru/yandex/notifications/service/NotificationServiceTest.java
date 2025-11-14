package ru.yandex.notifications.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {
    private final NotificationService service = new NotificationService();

    @Test
    void notifyDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> service.notify("user", "message"));
    }
}
