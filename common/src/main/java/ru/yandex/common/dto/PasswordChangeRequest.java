package ru.yandex.common.dto;

public record PasswordChangeRequest(String password, String confirm_password) {
}