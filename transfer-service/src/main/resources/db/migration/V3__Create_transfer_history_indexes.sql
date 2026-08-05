-- ======================================================================
-- TỐI ƯU HÓA LỊCH SỬ GIAO DỊCH (TRANSACTION HISTORY)
-- Thay vì tạo 1 Composite Index (from_account, to_account) sẽ bị lỗi Full Table Scan khi dùng OR.
-- Chúng ta tạo 2 Index rời, kết hợp với câu lệnh UNION ALL ở tầng Code Java.
-- Điều này giúp giảm I/O Cost hàng ngàn lần trên tập dữ liệu lớn.
-- ======================================================================

CREATE INDEX idx_transfers_from_account ON transfers(from_account);
CREATE INDEX idx_transfers_to_account ON transfers(to_account);
