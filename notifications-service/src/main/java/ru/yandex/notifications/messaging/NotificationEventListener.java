package ru.yandex.notifications.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.yandex.common.kafka.NotificationEvent;
import ru.yandex.notifications.service.NotificationService;

@Component
public class NotificationEventListener {
    private final NotificationService service;

    public NotificationEventListener(NotificationService service) {
        this.service = service;
    }

    @KafkaListener(
            topics = "${app.kafka.notifications-topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(NotificationEvent event, Acknowledgment ack) {
        service.notify(event.login(), event.message());
        ack.acknowledge();
    }
}