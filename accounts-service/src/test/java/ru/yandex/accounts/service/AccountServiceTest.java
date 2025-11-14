package ru.yandex.accounts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.yandex.accounts.domain.Account;
import ru.yandex.accounts.domain.Balance;
import ru.yandex.accounts.repository.AccountRepository;
import ru.yandex.accounts.repository.BalanceRepository;
import ru.yandex.common.dto.AccountCreateRequest;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private AccountService service;

    @Test
    void signupCreatesAccountAndBalance() {
        when(accountRepository.findByLogin("user")).thenReturn(Optional.empty());
        when(encoder.encode("pwd")).thenReturn("hash");
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Account.class));

        AccountCreateRequest request = new AccountCreateRequest(
                "user",
                "pwd",
                "User Name",
                LocalDate.of(2000, 1, 1).toString()
        );

        var dto = service.signup(request);

        assertThat(dto.login()).isEqualTo("user");
        assertThat(dto.name()).isEqualTo("User Name");
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void adjustAddsMoney() {
        Account account = new Account();
        account.setLogin("user");
        var id = UUID.randomUUID();
        var balance = new Balance();
        balance.setAccountId(id);
        balance.setAmount(BigDecimal.ZERO);

        when(accountRepository.findByLogin("user")).thenReturn(Optional.of(account));
        when(balanceRepository.findById(id)).thenReturn(Optional.of(balance));

        service.adjust("user", new BigDecimal("10.00"));

        assertThat(balance.getAmount()).isEqualByComparingTo("10.00");
    }

    @Test
    void adjustFailsOnInsufficientFunds() {
        Account account = new Account();
        account.setLogin("user");
        var id = UUID.randomUUID();
        var balance = new Balance();
        balance.setAccountId(id);
        balance.setAmount(new BigDecimal("5.00"));

        when(accountRepository.findByLogin("user")).thenReturn(Optional.of(account));
        when(balanceRepository.findById(id)).thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> service.adjust("user", new BigDecimal("-10.00")))
                .isInstanceOf(IllegalStateException.class);
    }
}
