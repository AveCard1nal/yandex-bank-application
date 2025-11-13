package ru.yandex.front.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.common.dto.AccountCreateRequest;
import ru.yandex.front.client.AccountsClient;

@Controller
public class SignupController {

    private final AccountsClient accounts;

    public SignupController(AccountsClient accounts) {
        this.accounts = accounts;
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam("login") String login,
                         @RequestParam("password") String password,
                         @RequestParam("confirm_password") String confirmPassword,
                         @RequestParam("name") String name,
                         @RequestParam("birthdate") String birthdate,
                         Model model) {
        List<String> errors = new ArrayList<>();

        if (login == null || login.isBlank()) {
            errors.add("Login is required");
        }
        if (password == null || password.isBlank()) {
            errors.add("Password is required");
        }
        if (password != null && !password.equals(confirmPassword)) {
            errors.add("Passwords do not match");
        }

        LocalDate date = null;
        try {
            date = LocalDate.parse(birthdate);
        } catch (Exception e) {
            errors.add("Invalid birthdate");
        }

        if (date != null) {
            int age = Period.between(date, LocalDate.now()).getYears();
            if (age < 18) {
                errors.add("Age must be 18 or older");
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("login", login);
            model.addAttribute("name", name);
            model.addAttribute("birthdate", birthdate);
            return "signup";
        }

        accounts.signup(new AccountCreateRequest(login, password, name, birthdate));
        return "redirect:/main?login=" + login;
    }

    @PostMapping("/login")
    public String login(@RequestParam("login") String login,
                        @RequestParam("password") String password,
                        Model model) {
        try {
            accounts.auth(Map.of("login", login, "password", password));
            return "redirect:/main?login=" + login;
        } catch (Exception ex) {
            model.addAttribute("login_error", true);
            model.addAttribute("login", login);
            return "signup";
        }
    }
}
