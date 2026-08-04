package com.tplite.banking.accountservice.controller;

import com.tplite.banking.accountservice.service.AccountService;
import com.tplite.banking.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{accountNumber}/hold")
    public ApiResponse<String> hold(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.holdMoney(accountNumber, amount);
        return ApiResponse.success("Đã đóng băng thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/clear")
    public ApiResponse<String> clear(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.clearMoney(accountNumber, amount);
        return ApiResponse.success("Đã xóa sổ (trừ tiền) thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/release")
    public ApiResponse<String> release(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.releaseMoney(accountNumber, amount);
        return ApiResponse.success("Đã hoàn trả (nhả tiền đóng băng) thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/credit")
    public ApiResponse<String> credit(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.creditMoney(accountNumber, amount);
        return ApiResponse.success("Đã cộng tiền thành công " + amount + " VND");
    }
}
