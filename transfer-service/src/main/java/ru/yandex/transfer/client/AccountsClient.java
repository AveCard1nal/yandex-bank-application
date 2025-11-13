package ru.yandex.transfer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gateway")
public interface AccountsClient {

    @PostMapping("/api/accounts/adjust")
    void adjust(@RequestParam("login") String login,
                @RequestParam("amount") String amount);
}