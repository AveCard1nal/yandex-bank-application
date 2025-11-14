package ru.yandex.accounts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.accounts.domain.Account;

@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void saveAndFindByLogin() {
        Account account = new Account();
        account.setLogin("user");
        account.setPassHash("hash");
        account.setName("User");
        account.setBirthdate(LocalDate.of(2000, 1, 1));

        accountRepository.save(account);

        var found = accountRepository.findByLogin("user");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("User");
    }
}
