CREATE TABLE outbox_events (
    id VARCHAR(36) PRIMARY KEY, -- UUID
    aggregate_type VARCHAR(255) NOT NULL, -- Tên Entity (VD: Transfer)
    aggregate_id VARCHAR(255) NOT NULL, -- ID của giao dịch
    type VARCHAR(255) NOT NULL, -- Tên Event (VD: TransferCreated)
    payload TEXT NOT NULL, -- Nội dung JSON của tin nhắn
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
