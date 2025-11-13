package ru.yandex.accounts.repo;

import ru.yandex.accounts.domain.Balance;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {
}
