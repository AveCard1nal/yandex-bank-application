package ru.yandex.common.dto;

public record AccountCreateRequest(String login, String password, String name, String birthdate) {
}