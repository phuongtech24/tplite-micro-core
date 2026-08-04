package com.tplite.banking.transferservice.repository;

import com.tplite.banking.transferservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    
    // Lấy tất cả sự kiện chưa gửi (sắp xếp theo thời gian cũ nhất lên trước)
    List<OutboxEvent> findAllByOrderByCreatedAtAsc();
}
