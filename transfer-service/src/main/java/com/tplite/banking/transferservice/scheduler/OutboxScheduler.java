package com.tplite.banking.transferservice.scheduler;

import com.tplite.banking.transferservice.entity.OutboxEvent;
import com.tplite.banking.transferservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Chạy ngầm mỗi 5 giây
    @Scheduled(fixedDelay = 5000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findAllByOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            try {
                // Ném vào Kafka, Topic = event.getType() (ví dụ: credit-requested)
                kafkaTemplate.send(event.getType(), event.getPayload());

                // Gửi xong thì xóa khỏi bảng Outbox để lần sau không gửi lại
                outboxEventRepository.delete(event);
                
                System.out.println("[OUTBOX] Đã gửi thành công sự kiện: " + event.getId());
            } catch (Exception e) {
                // Nếu lỗi mạng Kafka, kệ nó, tí nữa 5 giây sau gửi lại
                System.err.println("[OUTBOX] Lỗi gửi sự kiện lên Kafka: " + e.getMessage());
            }
        }
    }
}
