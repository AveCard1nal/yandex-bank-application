package ru.yandex.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import ru.yandex.transfer.client.AccountsClient;

@Service
public class TransferService {

    private final AccountsClient accounts;

    public TransferService(AccountsClient accounts) {
        this.accounts = accounts;
    }

    @CircuitBreaker(name = "transfer", fallbackMethod = "fallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal value) {
        accounts.adjust(fromLogin, value.negate().toPlainString());
        accounts.adjust(toLogin, value.toPlainString());
    }

    public void fallback(String fromLogin, String toLogin, BigDecimal value, Throwable ex) {
    }
}