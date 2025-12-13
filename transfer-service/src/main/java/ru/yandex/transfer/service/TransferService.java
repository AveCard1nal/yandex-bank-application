package ru.yandex.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import ru.yandex.common.kafka.NotificationType;
import ru.yandex.transfer.client.AccountsClient;
import ru.yandex.transfer.messaging.NotificationEventPublisher;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final AccountsClient accounts;
    private final NotificationEventPublisher publisher;

    public TransferService(AccountsClient accounts, NotificationEventPublisher publisher) {
        this.accounts = accounts;
        this.publisher = publisher;
    }

    @CircuitBreaker(name = "transfer", fallbackMethod = "fallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal value) {
        accounts.adjust(fromLogin, value.negate().toPlainString());
        accounts.adjust(toLogin, value.toPlainString());

        publisher.publish(
                NotificationType.TRANSFER_OUTGOING,
                fromLogin,
                "Transfer " + value.toPlainString() + " to " + toLogin
        );
        publisher.publish(
                NotificationType.TRANSFER_INCOMING,
                toLogin,
                "Receive " + value.toPlainString() + " from " + fromLogin
        );
    }

    public void fallback(String fromLogin, String toLogin, BigDecimal value, Throwable ex) {
    }
}