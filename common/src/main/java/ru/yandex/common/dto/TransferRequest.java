package ru.yandex.common.dto;

import java.math.BigDecimal;

public record TransferRequest(String login, String to_login, BigDecimal value) {
}