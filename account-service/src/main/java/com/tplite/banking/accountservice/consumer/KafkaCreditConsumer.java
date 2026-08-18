package com.tplite.banking.accountservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tplite.banking.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaCreditConsumer {

    private final AccountService accountService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "credit-requested", groupId = "account-group")
    public void consumeCreditRequest(String message) {
        log.info("📩 Nhận yêu cầu cộng tiền (credit-requested): {}", message);
        try {
            // Parse message
            Map<String, Object> payload = objectMapper.readValue(message, Map.class);
            String transferId = (String) payload.get("transferId");
            String toAccount = (String) payload.get("toAccount");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());

            try {
                // Thực hiện cộng tiền
                accountService.creditMoney(toAccount, amount, transferId);
                log.info("✅ Cộng tiền thành công cho tài khoản: {}", toAccount);
                
                // Báo cáo thành công về Kafka
                Map<String, Object> successPayload = new HashMap<>();
                successPayload.put("transferId", transferId);
                successPayload.put("status", "SUCCESS");
                kafkaTemplate.send("credit-success", objectMapper.writeValueAsString(successPayload));
                
            } catch (Exception e) {
                log.error("❌ Cộng tiền thất bại cho tài khoản {}: {}", toAccount, e.getMessage());
                
                // Báo cáo thất bại về Kafka (để Transfer Service gọi API Refund)
                Map<String, Object> failedPayload = new HashMap<>();
                failedPayload.put("transferId", transferId);
                failedPayload.put("status", "FAILED");
                failedPayload.put("reason", e.getMessage());
                kafkaTemplate.send("credit-failed", objectMapper.writeValueAsString(failedPayload));
            }
        } catch (Exception e) {
            log.error("Lỗi parse message credit-requested: {}", e.getMessage());
        }
    }
}
