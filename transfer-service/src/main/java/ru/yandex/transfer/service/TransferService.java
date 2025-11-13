package ru.yandex.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import ru.yandex.transfer.client.AccountsClient;
import ru.yandex.transfer.client.NotificationsClient;

@Service
public class TransferService {

    private final AccountsClient accounts;
    private final NotificationsClient notifications;

    public TransferService(AccountsClient accounts, NotificationsClient notifications) {
        this.accounts = accounts;
        this.notifications = notifications;
    }

    @CircuitBreaker(name = "transfer", fallbackMethod = "fallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal value) {
        accounts.adjust(fromLogin, value.negate().toPlainString());
        accounts.adjust(toLogin, value.toPlainString());

        notifications.notify(new NotificationsClient.NotificationRequest(
                fromLogin,
                "Transfer " + value.toPlainString() + " to " + toLogin
        ));
        notifications.notify(new NotificationsClient.NotificationRequest(
                toLogin,
                "Receive " + value.toPlainString() + " from " + fromLogin
        ));
    }

    public void fallback(String fromLogin, String toLogin, BigDecimal value, Throwable ex) {
    }
}