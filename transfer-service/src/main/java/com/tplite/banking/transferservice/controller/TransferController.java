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
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) com.tplite.banking.transferservice.enums.TransferStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        java.time.LocalDateTime start = startDate != null ? java.time.LocalDate.parse(startDate).atStartOfDay() : null;
        java.time.LocalDateTime end = endDate != null ? java.time.LocalDate.parse(endDate).atTime(23, 59, 59) : null;

        Page<Transfer> history = transferService.getTransactionHistoryWithFilter(accountNumber, minAmount, maxAmount, start, end, status, page, size);
        return ApiResponse.success(history);
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public org.springframework.http.ResponseEntity<String> exportCsv(
            @RequestParam String accountNumber,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) com.tplite.banking.transferservice.enums.TransferStatus status) {
        
        java.time.LocalDateTime start = startDate != null ? java.time.LocalDate.parse(startDate).atStartOfDay() : null;
        java.time.LocalDateTime end = endDate != null ? java.time.LocalDate.parse(endDate).atTime(23, 59, 59) : null;

        String csvData = transferService.exportToCsv(accountNumber, minAmount, maxAmount, start, end, status);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions_" + accountNumber + ".csv\"");
        headers.add(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
        
        // Add UTF-8 BOM for Excel
        return new org.springframework.http.ResponseEntity<>("\uFEFF" + csvData, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/statement/count")
    public ApiResponse<Long> countStatement(
            @RequestParam String accountNumber,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        java.time.LocalDateTime start = java.time.LocalDate.parse(startDate).atStartOfDay();
        java.time.LocalDateTime end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
        
        long count = transferService.countTransactionsByDateRange(accountNumber, start, end);
        return ApiResponse.success(count);
    }
}
