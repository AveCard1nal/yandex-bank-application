package ru.yandex.cash.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import ru.yandex.cash.client.AccountsClient;

@Service
public class CashService {
    private final AccountsClient accountsClient;

    public CashService(AccountsClient accountsClient) {
        this.accountsClient = accountsClient;
    }

    @CircuitBreaker(name = "cashAdjust", fallbackMethod = "fallback")
    public void cash(String login, BigDecimal value, String action) {
        if ("PUT".equalsIgnoreCase(action)) {
            accountsClient.adjust(login, value.toPlainString());
        } else if ("GET".equalsIgnoreCase(action)) {
            accountsClient.adjust(login, value.negate().toPlainString());
        } else {
            throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    public void fallback(String login, BigDecimal value, String action, Throwable throwable) {
    }
}
