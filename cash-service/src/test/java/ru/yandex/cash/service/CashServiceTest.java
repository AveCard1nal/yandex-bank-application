package ru.yandex.cash.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.cash.client.AccountsClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @Mock
    private AccountsClient accountsClient;

    @InjectMocks
    private CashService service;

    @Test
    void cashPutCallsAdjustWithPositiveAmount() {
        service.cash("user", new BigDecimal("10.00"), "PUT");

        verify(accountsClient).adjust("user", "10.00");
    }

    @Test
    void cashGetCallsAdjustWithNegativeAmount() {
        service.cash("user", new BigDecimal("10.00"), "GET");

        verify(accountsClient).adjust("user", "-10.00");
    }

    @Test
    void cashUnknownActionThrows() {
        assertThatThrownBy(() -> service.cash("user", new BigDecimal("10.00"), "XXX"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
