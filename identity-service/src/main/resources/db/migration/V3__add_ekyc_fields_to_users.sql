ALTER TABLE users ADD (
    full_name VARCHAR2(100),
    id_card_number VARCHAR2(20),
    ekyc_status VARCHAR2(20) DEFAULT 'UNVERIFIED' NOT NULL
);
