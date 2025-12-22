package ru.yandex.front.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.common.dto.AccountDto;
import ru.yandex.common.dto.AccountUpdateRequest;
import ru.yandex.common.dto.BalanceDto;
import ru.yandex.common.dto.CashRequest;
import ru.yandex.common.dto.PasswordChangeRequest;
import ru.yandex.common.dto.TransferRequest;
import ru.yandex.common.dto.UserBrief;
import ru.yandex.front.client.AccountsClient;
import ru.yandex.front.client.CashClient;
import ru.yandex.front.client.TransferClient;

@Controller
@Slf4j
public class MainController {

    private final AccountsClient accounts;
    private final CashClient cash;
    private final TransferClient transfers;

    public MainController(AccountsClient accounts, CashClient cash, TransferClient transfers) {
        this.accounts = accounts;
        this.cash = cash;
        this.transfers = transfers;
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/signup";
    }

    @GetMapping("/main")
    public String mainPage(@RequestParam("login") String login, Model model) {
        log.info("Accessing main page for login: {}", login);
        log.debug("Fetching account info for {}", login);
        AccountDto me = accounts.me(login);
        BalanceDto balance = accounts.balance(login);
        List<UserBrief> users = accounts.users(login);

        model.addAttribute("login", me.login());
        model.addAttribute("name", me.name());
        model.addAttribute("birthdate", me.birthdate());
        model.addAttribute("balance", balance.amount());
        model.addAttribute("users", users);

        return "main";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("login") String login,
                                @RequestParam("name") String name,
                                @RequestParam("birthdate") String birthdate) {
        log.info("Updating profile for login: {}", login);
        log.debug("Profile update details: name={}, birthdate={}", name, birthdate);
        accounts.updateProfile(login, new AccountUpdateRequest(name, birthdate));
        return "redirect:/main?login=" + login;
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam("login") String login,
                                 @RequestParam("password") String password,
                                 @RequestParam("confirm_password") String confirmPassword,
                                 Model model) {
        log.info("Changing password for login: {}", login);
        List<String> errors = new ArrayList<>();
        if (password == null || password.isBlank()) {
            errors.add("Password is required");
        }
        if (password != null && !password.equals(confirmPassword)) {
            errors.add("Passwords do not match");
        }
        if (!errors.isEmpty()) {
            log.warn("Password change validation failed for {}: {}", login, errors);
            model.addAttribute("password_errors", errors);
            return "redirect:/main?login=" + login;
        }
        try {
            accounts.changePassword(login, new PasswordChangeRequest(password, confirmPassword));
            log.info("Password changed successfully for {}", login);
        } catch (Exception e) {
            log.error("Failed to change password for {}: {}", login, e.getMessage());
            throw e;
        }
        return "redirect:/main?login=" + login;
    }

    @PostMapping("/cash")
    public String cash(@RequestParam("login") String login,
                       @RequestParam("value") String value,
                       @RequestParam("action") String action) {
        log.info("Cash operation requested: {} for login: {}, amount: {}", action, login, value);
        BigDecimal amount = new BigDecimal(value);
        cash.cash(new CashRequest(login, amount, action));
        return "redirect:/main?login=" + login;
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam("login") String fromLogin,
                           @RequestParam("to_login") String toLogin,
                           @RequestParam("value") String value) {
        log.info("Transfer requested from {} to {} amount {}", fromLogin, toLogin, value);
        BigDecimal amount = new BigDecimal(value);
        transfers.transfer(new TransferRequest(fromLogin, toLogin, amount));
        return "redirect:/main?login=" + fromLogin;
    }
}
