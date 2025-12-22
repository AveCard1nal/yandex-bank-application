package ru.yandex.accounts.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AccountsController {

    private final AccountService service;
    private final MeterRegistry meterRegistry;

    public AccountsController(AccountService service, MeterRegistry meterRegistry) {
        this.service = service;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/signup")
    public AccountDto signup(@RequestBody AccountCreateRequest request) {
        log.info("Signup request for login: {}", request.login());
        log.debug("Signup details: {}", request);
        return service.signup(request);
    }

    @PostMapping("/auth")
    public void auth(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        log.info("Auth request for login: {}", login);
        try {
            boolean ok = service.auth(login, body.get("password"));
            if (!ok) {
                log.warn("Auth failed for login: {}", login);
                Counter.builder("login.failure")
                        .tag("login", login)
                        .register(meterRegistry)
                        .increment();
                throw new RuntimeException("bad_credentials");
            }
            log.info("Auth success for login: {}", login);
            Counter.builder("login.success")
                    .tag("login", login)
                    .register(meterRegistry)
                    .increment();
        } catch (Exception e) {
            log.error("Error during auth for login: {}", login, e);
            if (!"bad_credentials".equals(e.getMessage())) {
                Counter.builder("login.failure")
                        .tag("login", login)
                        .register(meterRegistry)
                        .increment();
            }
            throw e;
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