package com.tplite.banking.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    // Lắng nghe hộp thư mang tên "transfer-events"
    @KafkaListener(topics = "transfer-events", groupId = "notification-group")
    public void listen(String message) {
        System.out.println("=========================================");
        System.out.println("📩 TING TING! CÓ TIN NHẮN TỪ KAFKA!");
        System.out.println("📝 Nội dung: " + message);
        System.out.println("🚀 Tiến hành gửi Email/SMS cho khách hàng...");
        System.out.println("=========================================");
    }
}
