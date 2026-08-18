package com.tplite.banking.identityservice.client;

import com.tplite.banking.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "account-service", path = "/api/v1/accounts")
public interface AccountClient {
    
    @PostMapping
    ApiResponse<String> createAccount(@RequestParam("userId") UUID userId);
}
