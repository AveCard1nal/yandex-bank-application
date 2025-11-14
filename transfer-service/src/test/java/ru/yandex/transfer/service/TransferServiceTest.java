package ru.yandex.transfer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.transfer.client.AccountsClient;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountsClient accountsClient;

    @InjectMocks
    private TransferService service;

    @Test
    void transferCallsAdjustForBothUsers() {
        service.transfer("from", "to", new BigDecimal("15.00"));

        verify(accountsClient).adjust("from", "-15.00");
        verify(accountsClient).adjust("to", "15.00");
    }
}
