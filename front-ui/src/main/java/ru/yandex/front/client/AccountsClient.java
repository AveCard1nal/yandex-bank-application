package ru.yandex.front.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.common.dto.AccountCreateRequest;
import ru.yandex.common.dto.AccountDto;
import ru.yandex.common.dto.AccountUpdateRequest;
import ru.yandex.common.dto.BalanceDto;
import ru.yandex.common.dto.PasswordChangeRequest;
import ru.yandex.common.dto.UserBrief;

@FeignClient(name = "gateway")
public interface AccountsClient {
    @PostMapping("/api/accounts/signup")
    AccountDto signup(@RequestBody AccountCreateRequest request);

    @PostMapping("/api/accounts/auth")
    void auth(@RequestBody Map<String, String> body);

    @GetMapping("/api/accounts/me")
    AccountDto me(@RequestParam("login") String login);

    @GetMapping("/api/accounts/me/balance")
    BalanceDto balance(@RequestParam("login") String login);

    @GetMapping("/api/accounts/users")
    List<UserBrief> users(@RequestParam("exclude") String excludeLogin);

    @PostMapping("/api/accounts/{login}/password")
    void changePassword(@PathVariable("login") String login,
                        @RequestBody PasswordChangeRequest request);

    @PostMapping("/api/accounts/{login}")
    void updateProfile(@PathVariable("login") String login,
                       @RequestBody AccountUpdateRequest request);
}
