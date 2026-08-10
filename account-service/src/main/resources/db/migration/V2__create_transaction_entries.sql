CREATE TABLE transaction_entries (
    id VARCHAR2(36) PRIMARY KEY,
    account_number VARCHAR2(30) NOT NULL,
    type VARCHAR2(20) NOT NULL,
    amount NUMBER(19, 2) NOT NULL,
    balance_after NUMBER(19, 2) NOT NULL,
    reference_id VARCHAR2(36),
    description VARCHAR2(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR2(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by VARCHAR2(50)
);

CREATE INDEX idx_transaction_account ON transaction_entries(account_number);
