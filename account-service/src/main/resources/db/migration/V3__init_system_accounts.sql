-- Khởi tạo các tài khoản kế toán hệ thống (System GL Accounts)
-- userId được để null hoặc một UUID giả định cho hệ thống

INSERT INTO accounts (id, user_id, account_number, balance, currency, status, created_at, updated_at) 
VALUES (SYS_GUID(), '00000000-0000-0000-0000-000000000000', '101000', 1000000000, 'VND', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (id, user_id, account_number, balance, currency, status, created_at, updated_at) 
VALUES (SYS_GUID(), '00000000-0000-0000-0000-000000000000', '299000', 0, 'VND', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (id, user_id, account_number, balance, currency, status, created_at, updated_at) 
VALUES (SYS_GUID(), '00000000-0000-0000-0000-000000000000', '701000', 0, 'VND', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (id, user_id, account_number, balance, currency, status, created_at, updated_at) 
VALUES (SYS_GUID(), '00000000-0000-0000-0000-000000000000', '301000', 0, 'VND', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
