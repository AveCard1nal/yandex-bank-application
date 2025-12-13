package ru.yandex.common.kafka;

import java.time.Instant;
import java.util.UUID;

public record NotificationEvent(
        UUID eventId,
        NotificationType type,
        Instant occurredAt,
        String login,
        String message
) {
    public static NotificationEvent of(NotificationType type, String login, String message) {
        return new NotificationEvent(UUID.randomUUID(), type, Instant.now(), login, message);
    }
}