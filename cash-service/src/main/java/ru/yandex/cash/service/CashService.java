package ru.yandex.cash.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import ru.yandex.cash.client.AccountsClient;
import ru.yandex.cash.client.NotificationsClient;

@Service
public class CashService {

    private final AccountsClient accounts;
    private final NotificationsClient notifications;

    public CashService(AccountsClient accounts, NotificationsClient notifications) {
        this.accounts = accounts;
        this.notifications = notifications;
    }

    @CircuitBreaker(name = "cash", fallbackMethod = "fallback")
    public void cash(String login, BigDecimal value, String action) {
        if ("PUT".equalsIgnoreCase(action)) {
            accounts.adjust(login, value.toPlainString());
            notifications.notify(new NotificationsClient.NotificationRequest(
                    login,
                    "Deposit " + value.toPlainString()
            ));
        } else if ("GET".equalsIgnoreCase(action)) {
            accounts.adjust(login, value.negate().toPlainString());
            notifications.notify(new NotificationsClient.NotificationRequest(
                    login,
                    "Withdraw " + value.toPlainString()
            ));
        } else {
            throw new IllegalArgumentException("unknown_action");
        }
    }

    public void fallback(String login, BigDecimal value, String action, Throwable ex) {
    }
}