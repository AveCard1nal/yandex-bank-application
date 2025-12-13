package ru.yandex.cash;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.yandex.notifications.service.NotificationService;

@TestConfiguration
public class NotificationServiceSpyTestConfig {
    @Bean
    @Primary
    public NotificationService notificationServiceSpy(NotificationService delegate) {
        return Mockito.spy(delegate);
    }
}