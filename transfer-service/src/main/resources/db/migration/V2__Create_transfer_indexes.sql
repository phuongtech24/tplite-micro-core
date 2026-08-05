-- Tạo Composite Index để tăng tốc độ tra cứu lịch sử giao dịch của 1 tài khoản
CREATE INDEX idx_transfer_accounts ON transfer(from_account, to_account);
