package ru.yandex.accounts.service;

import ru.yandex.accounts.domain.Account;
import ru.yandex.accounts.domain.Balance;
import ru.yandex.accounts.repo.AccountRepository;
import ru.yandex.accounts.repo.BalanceRepository;
import ru.yandex.common.dto.AccountCreateRequest;
import ru.yandex.common.dto.AccountDto;
import ru.yandex.common.dto.AccountUpdateRequest;
import ru.yandex.common.dto.BalanceDto;
import ru.yandex.common.dto.PasswordChangeRequest;
import ru.yandex.common.dto.UserBrief;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accounts;
    private final BalanceRepository balances;
    private final BCryptPasswordEncoder encoder;

    public AccountService(AccountRepository accounts, BalanceRepository balances, BCryptPasswordEncoder encoder) {
        this.accounts = accounts;
        this.balances = balances;
        this.encoder = encoder;
    }

    @Transactional
    public AccountDto signup(AccountCreateRequest request) {
        if (accounts.findByLogin(request.login()).isPresent()) {
            throw new IllegalStateException("login");
        }

        Account account = new Account();
        account.setLogin(request.login());
        account.setPassHash(encoder.encode(request.password()));
        account.setName(request.name());
        account.setBirthdate(LocalDate.parse(request.birthdate()));
        account = accounts.save(account);

        Balance balance = new Balance();
        balance.setAccountId(account.getId());
        balance.setAmount(BigDecimal.ZERO);
        balances.save(balance);

        return new AccountDto(account.getId(), account.getLogin(), account.getName(), account.getBirthdate());
    }

    @Transactional(readOnly = true)
    public boolean auth(String login, String password) {
        Account account = accounts.findByLogin(login).orElse(null);
        return account != null && encoder.matches(password, account.getPassHash());
    }

    @Transactional(readOnly = true)
    public AccountDto me(String login) {
        Account account = accounts.findByLogin(login).orElseThrow();
        return new AccountDto(account.getId(), account.getLogin(), account.getName(), account.getBirthdate());
    }

    @Transactional(readOnly = true)
    public BalanceDto balance(String login) {
        Account account = accounts.findByLogin(login).orElseThrow();
        Balance balance = balances.findById(account.getId()).orElseThrow();
        return new BalanceDto(account.getId(), balance.getAmount());
    }

    @Transactional
    public void changePassword(String login, PasswordChangeRequest request) {
        if (!request.password().equals(request.confirm_password())) {
            throw new IllegalStateException("password_mismatch");
        }
        Account account = accounts.findByLogin(login).orElseThrow();
        account.setPassHash(encoder.encode(request.password()));
    }

    @Transactional
    public void update(String login, AccountUpdateRequest request) {
        Account account = accounts.findByLogin(login).orElseThrow();
        account.setName(request.name());
        account.setBirthdate(LocalDate.parse(request.birthdate()));
    }

    @Transactional(readOnly = true)
    public List<UserBrief> users(String excludeLogin) {
        return accounts.findTop50ByLoginNotOrderByLoginAsc(excludeLogin).stream()
                .map(a -> new UserBrief(a.getLogin(), a.getName()))
                .toList();
    }

    @Transactional
    public void adjust(String login, BigDecimal delta) {
        Account account = accounts.findByLogin(login).orElseThrow();
        Balance balance = balances.findById(account.getId()).orElseThrow();
        BigDecimal next = balance.getAmount().add(delta);
        if (next.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("insufficient_funds");
        }
        balance.setAmount(next);
    }
}
