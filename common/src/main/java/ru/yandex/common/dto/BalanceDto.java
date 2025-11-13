package ru.yandex.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceDto(UUID accountId, BigDecimal amount) {
}