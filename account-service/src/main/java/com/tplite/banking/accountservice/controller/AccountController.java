package com.tplite.banking.accountservice.controller;

import com.tplite.banking.accountservice.dto.UpdateAccountStatusRequest;
import com.tplite.banking.accountservice.service.AccountService;
import com.tplite.banking.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final com.tplite.banking.accountservice.repository.TransactionEntryRepository transactionEntryRepository;

    @PostMapping
    public ApiResponse<String> createAccount(@RequestParam java.util.UUID userId) {
        String accountNumber = accountService.createAccount(userId);
        return ApiResponse.success(accountNumber);
    }

    @PostMapping("/{accountNumber}/hold")
    public ApiResponse<String> hold(@PathVariable String accountNumber, @RequestParam BigDecimal amount, @RequestParam(required = false) String referenceId) {
        accountService.holdMoney(accountNumber, amount, referenceId);
        return ApiResponse.success("Đã đóng băng thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/clear")
    public ApiResponse<String> clear(@PathVariable String accountNumber, @RequestParam BigDecimal amount, @RequestParam(required = false) String referenceId) {
        accountService.clearMoney(accountNumber, amount, referenceId);
        return ApiResponse.success("Đã xóa sổ (trừ tiền) thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/release")
    public ApiResponse<String> release(@PathVariable String accountNumber, @RequestParam BigDecimal amount, @RequestParam(required = false) String referenceId) {
        accountService.releaseMoney(accountNumber, amount, referenceId);
        return ApiResponse.success("Đã hoàn trả (nhả tiền đóng băng) thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/credit")
    public ApiResponse<String> credit(@PathVariable String accountNumber, @RequestParam BigDecimal amount, @RequestParam(required = false) String referenceId) {
        accountService.creditMoney(accountNumber, amount, referenceId);
        return ApiResponse.success("Đã cộng tiền thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/deduct")
    public ApiResponse<String> deduct(@PathVariable String accountNumber, @RequestParam BigDecimal amount, @RequestParam(required = false) String referenceId) {
        accountService.deductMoney(accountNumber, amount, referenceId);
        return ApiResponse.success("Đã trừ tiền thành công " + amount + " VND");
    }

    @PutMapping("/{accountNumber}/status")
    public ApiResponse<String> updateStatus(
            @PathVariable String accountNumber,
            @Valid @RequestBody UpdateAccountStatusRequest request) {
        accountService.updateStatus(accountNumber, request.getStatus());
        return ApiResponse.success("Đã cập nhật trạng thái tài khoản thành công!");
    }

    @GetMapping("/{accountNumber}/audit")
    public ApiResponse<String> auditBalance(@PathVariable String accountNumber) {
        BigDecimal actualBalance = transactionEntryRepository.calculateActualBalance(accountNumber);
        BigDecimal currentBalance = accountService.getAccountByNumber(accountNumber).getBalance();
        return ApiResponse.success(
            "Audit thành công! Số dư trên tài khoản: " + currentBalance + 
            " VND. Tổng đối chiếu Sổ cái (SUM Ledger): " + actualBalance + " VND. " +
            (actualBalance.compareTo(currentBalance) == 0 ? "✅ KHỚP!" : "❌ LỆCH!")
        );
    }
}
