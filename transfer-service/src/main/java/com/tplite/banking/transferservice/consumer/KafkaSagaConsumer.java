package com.tplite.banking.transferservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tplite.banking.transferservice.client.AccountClient;
import com.tplite.banking.transferservice.entity.Transfer;
import com.tplite.banking.transferservice.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSagaConsumer {

    private final TransferRepository transferRepository;
    private final AccountClient accountClient;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "credit-success", groupId = "transfer-group")
    public void consumeCreditSuccess(String message) {
        log.info("📩 [SAGA COMPLETION] Nhận phản hồi cộng tiền THÀNH CÔNG: {}", message);
        try {
            Map<String, Object> payload = objectMapper.readValue(message, Map.class);
            String transferId = (String) payload.get("transferId");

            Optional<Transfer> transferOpt = transferRepository.findById(UUID.fromString(transferId));
            if (transferOpt.isPresent()) {
                Transfer transfer = transferOpt.get();
                transfer.setStatus("COMPLETED");
                transferRepository.save(transfer);
                log.info("✅ Đã chốt giao dịch {} thành COMPLETED.", transferId);
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý credit-success: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "credit-failed", groupId = "transfer-group")
    public void consumeCreditFailed(String message) {
        log.info("🚨 [SAGA COMPENSATING] Nhận phản hồi cộng tiền THẤT BẠI: {}", message);
        try {
            Map<String, Object> payload = objectMapper.readValue(message, Map.class);
            String transferId = (String) payload.get("transferId");
            String reason = (String) payload.get("reason");

            Optional<Transfer> transferOpt = transferRepository.findById(UUID.fromString(transferId));
            if (transferOpt.isPresent()) {
                Transfer transfer = transferOpt.get();
                transfer.setStatus("FAILED");
                transfer.setDescription("Hoàn tiền do lỗi cộng tiền: " + reason);
                transferRepository.save(transfer);

                // THỰC THI GIAO DỊCH BÙ TRỪ (HOÀN TIỀN LẠI CHO NGƯỜI GỬI)
                log.info("🔙 Đang tiến hành HOÀN TIỀN cho tài khoản {} số tiền {}...", transfer.getFromAccount(), transfer.getAmount());
                accountClient.creditMoney(transfer.getFromAccount(), transfer.getAmount());
                log.info("✅ Đã hoàn tiền thành công cho tài khoản {}!", transfer.getFromAccount());
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý credit-failed (Có thể gây thất thoát tiền nếu không xử lý kỹ): {}", e.getMessage());
        }
    }
}
