package ru.yandex.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.common.kafka.NotificationType;
import ru.yandex.transfer.client.AccountsClient;
import ru.yandex.transfer.messaging.NotificationEventPublisher;

import java.math.BigDecimal;

@Service
@Slf4j
public class TransferService {
    private final AccountsClient accounts;
    private final NotificationEventPublisher publisher;
    private final MeterRegistry meterRegistry;

    public TransferService(AccountsClient accounts, NotificationEventPublisher publisher, MeterRegistry meterRegistry) {
        this.accounts = accounts;
        this.publisher = publisher;
        this.meterRegistry = meterRegistry;
    }

    @CircuitBreaker(name = "transfer", fallbackMethod = "fallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal value) {
        log.info("Processing transfer from {} to {} amount {}", fromLogin, toLogin, value);
        log.debug("Transfer details: from={}, to={}, amount={}", fromLogin, toLogin, value);
        
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
        log.info("Transfer successful from {} to {}", fromLogin, toLogin);
    }

    public void fallback(String fromLogin, String toLogin, BigDecimal value, Throwable ex) {
        log.error("Transfer failed from {} to {} amount {}. Error: {}", fromLogin, toLogin, value, ex.getMessage());
        Counter.builder("transfer.failure")
                .tag("from", fromLogin)
                .tag("to", toLogin)
                .tag("from_account", fromLogin)
                .tag("to_account", toLogin)
                .register(meterRegistry)
                .increment();
    }
}