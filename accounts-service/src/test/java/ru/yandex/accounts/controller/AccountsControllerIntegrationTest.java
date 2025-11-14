package ru.yandex.accounts.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.common.dto.AccountCreateRequest;
import ru.yandex.common.dto.AccountDto;
import ru.yandex.common.dto.BalanceDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void signupAndGetBalance() {
        AccountCreateRequest request = new AccountCreateRequest(
                "user1",
                "pwd",
                "User One",
                LocalDate.of(2000, 1, 1).toString()
        );

        ResponseEntity<AccountDto> signupResponse =
                restTemplate.postForEntity("/api/accounts/signup", request, AccountDto.class);

        assertThat(signupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(signupResponse.getBody()).isNotNull();
        assertThat(signupResponse.getBody().login()).isEqualTo("user1");

        ResponseEntity<BalanceDto> balanceResponse =
                restTemplate.getForEntity("/api/accounts/me/balance?login=user1", BalanceDto.class);

        assertThat(balanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(balanceResponse.getBody()).isNotNull();
        assertThat(balanceResponse.getBody().amount().toPlainString()).isEqualTo("0");
    }
}
