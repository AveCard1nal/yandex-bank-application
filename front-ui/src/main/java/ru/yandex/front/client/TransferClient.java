package ru.yandex.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.common.dto.TransferRequest;

@FeignClient(name = "gateway")
public interface TransferClient {

    @PostMapping("/api/transfers")
    void transfer(@RequestBody TransferRequest request);
}