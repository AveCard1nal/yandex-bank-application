package ru.yandex.accounts.repository;

import ru.yandex.accounts.domain.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByLogin(String login);

    List<Account> findTop50ByLoginNotOrderByLoginAsc(String login);
}
