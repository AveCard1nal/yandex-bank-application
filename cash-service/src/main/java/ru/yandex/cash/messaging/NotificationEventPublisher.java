package ru.yandex.cash.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.common.kafka.NotificationEvent;
import ru.yandex.common.kafka.NotificationType;

@Component
public class NotificationEventPublisher {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String topic;

    public NotificationEventPublisher(KafkaTemplate<String, NotificationEvent> kafkaTemplate,
                                      @Value("${app.kafka.notifications-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(NotificationType type, String login, String message) {
        NotificationEvent event = NotificationEvent.of(type, login, message);
        kafkaTemplate.send(topic, login, event);
    }
}