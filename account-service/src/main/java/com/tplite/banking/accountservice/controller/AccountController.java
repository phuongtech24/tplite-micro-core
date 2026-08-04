package com.tplite.banking.accountservice.controller;

import com.tplite.banking.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{accountNumber}/hold")
    public ResponseEntity<String> hold(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.holdMoney(accountNumber, amount);
        return ResponseEntity.ok("Đã đóng băng thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/clear")
    public ResponseEntity<String> clear(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.clearMoney(accountNumber, amount);
        return ResponseEntity.ok("Đã xóa sổ (trừ tiền) thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/release")
    public ResponseEntity<String> release(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.releaseMoney(accountNumber, amount);
        return ResponseEntity.ok("Đã hoàn trả (nhả tiền đóng băng) thành công " + amount + " VND");
    }

    @PostMapping("/{accountNumber}/credit")
    public ResponseEntity<String> credit(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        accountService.creditMoney(accountNumber, amount);
        return ResponseEntity.ok("Đã cộng tiền thành công " + amount + " VND");
    }
}
