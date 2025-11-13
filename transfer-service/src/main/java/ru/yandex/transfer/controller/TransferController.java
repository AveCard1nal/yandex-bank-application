package ru.yandex.transfer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.common.dto.TransferRequest;
import ru.yandex.transfer.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService service;

    public TransferController(TransferService service) {
        this.service = service;
    }

    @PostMapping
    public void transfer(@RequestBody TransferRequest request) {
        service.transfer(request.login(), request.to_login(), request.value());
    }
}