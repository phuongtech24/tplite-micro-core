CREATE TABLE transfers (
    id UUID PRIMARY KEY,
    from_account VARCHAR(30) NOT NULL,
    to_account VARCHAR(30) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'VND',
    status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED, ROLLBACKED
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE idempotency_keys (
    idempotency_key VARCHAR(100) PRIMARY KEY,
    request_path VARCHAR(255) NOT NULL,
    response_status INT,
    response_body TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
