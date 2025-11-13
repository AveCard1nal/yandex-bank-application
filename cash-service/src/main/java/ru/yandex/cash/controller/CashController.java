package ru.yandex.cash.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.cash.service.CashService;
import ru.yandex.common.dto.CashRequest;

@RestController
@RequestMapping("/api/cash")
public class CashController {

    private final CashService cashService;

    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @PostMapping
    public void cash(@RequestBody CashRequest request) {
        cashService.cash(request.login(), request.value(), request.action());
    }
}
