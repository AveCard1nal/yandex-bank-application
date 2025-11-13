package ru.yandex.common.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AccountDto(UUID id, String login, String name, LocalDate birthdate) {
}