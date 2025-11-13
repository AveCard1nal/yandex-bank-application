package ru.yandex.common.dto;

import java.math.BigDecimal;

public record CashRequest(String login, BigDecimal value, String action) {
}