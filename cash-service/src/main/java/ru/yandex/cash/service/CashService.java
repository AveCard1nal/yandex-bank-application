package ru.yandex.cash.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.cash.client.AccountsClient;
import ru.yandex.cash.messaging.NotificationEventPublisher;
import ru.yandex.common.kafka.NotificationType;

import java.math.BigDecimal;

@Service
@Slf4j
public class CashService {
    private final AccountsClient accounts;
    private final NotificationEventPublisher publisher;

    public CashService(AccountsClient accounts, NotificationEventPublisher publisher) {
        this.accounts = accounts;
        this.publisher = publisher;
    }

    @CircuitBreaker(name = "cash", fallbackMethod = "fallback")
    public void cash(String login, BigDecimal value, String action) {
        log.info("Processing cash operation: {} for login: {}, amount: {}", action, login, value);
        log.debug("Cash details: login={}, amount={}, action={}", login, value, action);
        if ("PUT".equalsIgnoreCase(action)) {
            accounts.adjust(login, value.toPlainString());
            publisher.publish(NotificationType.CASH_DEPOSIT, login, "Deposit " + value.toPlainString());
        } else if ("GET".equalsIgnoreCase(action)) {
            accounts.adjust(login, value.negate().toPlainString());
            publisher.publish(NotificationType.CASH_WITHDRAW, login, "Withdraw " + value.toPlainString());
        } else {
            log.warn("Unknown cash action: {}", action);
            throw new IllegalArgumentException("unknown_action");
        }
        log.info("Cash operation {} completed for login: {}", action, login);
    }

    public void fallback(String login, BigDecimal value, String action, Throwable ex) {
        log.error("Cash operation failed for login: {}. Action: {}, Amount: {}. Error: {}", 
                login, action, value, ex.getMessage());
    }
}
