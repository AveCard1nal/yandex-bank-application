package ru.yandex.cash;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.yandex.cash.service.CashService;
import ru.yandex.notifications.NotificationsApplication;
import ru.yandex.notifications.service.NotificationService;

import java.math.BigDecimal;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@Testcontainers
class CashToNotificationsKafkaIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    static ConfigurableApplicationContext notificationsCtx;
    static ConfigurableApplicationContext cashCtx;

    @AfterAll
    static void stop() {
        if (cashCtx != null) {
            cashCtx.close();
        }
        if (notificationsCtx != null) {
            notificationsCtx.close();
        }
    }

    @Test
    void cashShouldPublishAndNotificationsShouldConsume() {
        notificationsCtx = new SpringApplicationBuilder(NotificationsApplication.class, NotificationServiceSpyTestConfig.class)
                .properties(
                        "server.port=0",
                        "app.kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
                        "app.kafka.notifications-topic=bank.notifications",
                        "app.kafka.group-id=notifications-it"
                )
                .run();

        NotificationService notificationService = notificationsCtx.getBean(NotificationService.class);

        cashCtx = new SpringApplicationBuilder(CashApplication.class)
                .properties(
                        "server.port=0",
                        "app.kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
                        "app.kafka.notifications-topic=bank.notifications"
                )
                .run();

        CashService cashService = cashCtx.getBean(CashService.class);

        cashService.cash("test", new BigDecimal("100"), "PUT");

        await()
                .atMost(10, SECONDS)
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() ->
                        verify(notificationService, atLeastOnce()).notify(eq("test"), eq("Deposit 100"))
                );
    }
}
