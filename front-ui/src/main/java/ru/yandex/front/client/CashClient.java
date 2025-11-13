package ru.yandex.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.common.dto.CashRequest;

@FeignClient(name = "gateway")
public interface CashClient {

    @PostMapping("/api/cash")
    void cash(@RequestBody CashRequest request);
}
