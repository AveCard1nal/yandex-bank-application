package ru.yandex.transfer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.yandex.notifications.NotificationsApplication;
import ru.yandex.notifications.service.NotificationService;
import ru.yandex.transfer.service.TransferService;

import java.math.BigDecimal;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@Testcontainers
class TransferToNotificationsKafkaIntegrationTest {
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    static ConfigurableApplicationContext notificationsCtx;
    static ConfigurableApplicationContext transferCtx;

    @AfterAll
    static void stop() {
        if (transferCtx != null) {
            transferCtx.close();
        }
        if (notificationsCtx != null) {
            notificationsCtx.close();
        }
    }

    @Test
    void transferShouldPublishAndNotificationsShouldConsume() {
        notificationsCtx = new SpringApplicationBuilder(NotificationsApplication.class, NotificationServiceSpyTestConfig.class)
                .properties(
                        "server.port=0",
                        "app.kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
                        "app.kafka.notifications-topic=bank.notifications",
                        "app.kafka.group-id=notifications-transfer-it"
                )
                .run();

        NotificationService notificationService = notificationsCtx.getBean(NotificationService.class);

        transferCtx = new SpringApplicationBuilder(TransferApplication.class)
                .properties(
                        "server.port=0",
                        "app.kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
                        "app.kafka.notifications-topic=bank.notifications"
                )
                .run();

        TransferService transferService = transferCtx.getBean(TransferService.class);

        transferService.transfer("fromUser", "toUser", new BigDecimal("5"));

        await()
                .atMost(10, SECONDS)
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    verify(notificationService, atLeastOnce()).notify(eq("fromUser"), eq("Transfer 5 to toUser"));
                    verify(notificationService, atLeastOnce()).notify(eq("toUser"), eq("Receive 5 from fromUser"));
                });
    }
}
