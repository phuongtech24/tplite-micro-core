package com.tplite.banking.transferservice.controller;

import com.tplite.banking.common.dto.ApiResponse;
import com.tplite.banking.transferservice.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import com.tplite.banking.transferservice.entity.Transfer;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ApiResponse<String> transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam BigDecimal amount) {
            
        String result = transferService.createTransfer(idempotencyKey, fromAccount, toAccount, amount);
        return ApiResponse.success(result);
    }

    @GetMapping("/history")
    public ApiResponse<Page<Transfer>> getHistory(
            @RequestParam String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Transfer> history = transferService.getTransactionHistory(accountNumber, page, size);
        return ApiResponse.success(history);
    }
}
