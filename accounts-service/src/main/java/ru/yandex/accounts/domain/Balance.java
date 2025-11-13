package ru.yandex.accounts.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "balance")
@Getter
@Setter
public class Balance {

    @Id
    private UUID accountId;

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Version
    private long version;
}
