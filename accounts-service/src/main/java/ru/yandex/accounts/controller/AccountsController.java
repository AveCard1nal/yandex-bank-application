package ru.yandex.accounts.web;

import ru.yandex.accounts.service.AccountService;
import ru.yandex.common.dto.AccountCreateRequest;
import ru.yandex.common.dto.AccountDto;
import ru.yandex.common.dto.AccountUpdateRequest;
import ru.yandex.common.dto.BalanceDto;
import ru.yandex.common.dto.PasswordChangeRequest;
import ru.yandex.common.dto.UserBrief;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    private final AccountService service;

    public AccountsController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public AccountDto signup(@RequestBody AccountCreateRequest request) {
        return service.signup(request);
    }

    @PostMapping("/auth")
    public void auth(@RequestBody Map<String, String> body) {
        boolean ok = service.auth(body.get("login"), body.get("password"));
        if (!ok) {
            throw new RuntimeException("bad_credentials");
        }
    }

    @GetMapping("/me")
    public AccountDto me(@RequestParam String login) {
        return service.me(login);
    }

    @GetMapping("/me/balance")
    public BalanceDto balance(@RequestParam String login) {
        return service.balance(login);
    }

    @GetMapping("/users")
    public List<UserBrief> users(@RequestParam String exclude) {
        return service.users(exclude);
    }

    @PostMapping("/{login}/password")
    public void changePassword(@PathVariable String login, @RequestBody PasswordChangeRequest request) {
        service.changePassword(login, request);
    }

    @PostMapping("/{login}")
    public void update(@PathVariable String login, @RequestBody AccountUpdateRequest request) {
        service.update(login, request);
    }

    @PostMapping("/adjust")
    public void adjust(@RequestParam String login, @RequestParam String amount) {
        service.adjust(login, new BigDecimal(amount));
    }
}