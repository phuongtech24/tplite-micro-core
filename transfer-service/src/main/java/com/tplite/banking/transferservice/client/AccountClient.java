package com.tplite.banking.transferservice.client;

import com.tplite.banking.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

// Khai báo tên service cần gọi (đã đăng ký trên Eureka)
@FeignClient(name = "account-service", path = "/api/v1/accounts")
public interface AccountClient {
    
    @PostMapping("/{accountNumber}/hold")
    ApiResponse<String> holdMoney(@PathVariable("accountNumber") String accountNumber, @RequestParam("amount") BigDecimal amount, @RequestParam(value = "referenceId", required = false) String referenceId);

    @PostMapping("/{accountNumber}/clear")
    ApiResponse<String> clearMoney(@PathVariable("accountNumber") String accountNumber, @RequestParam("amount") BigDecimal amount, @RequestParam(value = "referenceId", required = false) String referenceId);

    @PostMapping("/{accountNumber}/release")
    ApiResponse<String> releaseMoney(@PathVariable("accountNumber") String accountNumber, @RequestParam("amount") BigDecimal amount, @RequestParam(value = "referenceId", required = false) String referenceId);

    @PostMapping("/{accountNumber}/deduct")
    ApiResponse<String> deductMoney(@PathVariable("accountNumber") String accountNumber, @RequestParam("amount") BigDecimal amount, @RequestParam(value = "referenceId", required = false) String referenceId);

    @PostMapping("/{accountNumber}/credit")
    ApiResponse<String> creditMoney(@PathVariable("accountNumber") String accountNumber, @RequestParam("amount") BigDecimal amount, @RequestParam(value = "referenceId", required = false) String referenceId);
}
