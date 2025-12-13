package ru.yandex.notifications;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.yandex.common.kafka.NotificationEvent;
import ru.yandex.common.kafka.NotificationType;
import ru.yandex.notifications.service.NotificationService;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
class NotificationKafkaIntegrationTest {
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("app.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("app.kafka.notifications-topic", () -> "bank.notifications");
        registry.add("app.kafka.group-id", () -> "notifications-test");
    }

    @Autowired
    KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @MockitoSpyBean
    NotificationService notificationService;

    @Test
    void shouldConsumeNotificationEvent() {
        NotificationEvent event = NotificationEvent.of(NotificationType.CASH_DEPOSIT, "test", "hello");

        kafkaTemplate.send("bank.notifications", event.login(), event);

        await()
                .atMost(10, SECONDS)
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() ->
                        verify(notificationService, atLeastOnce()).notify(anyString(), anyString())
                );
    }
}
