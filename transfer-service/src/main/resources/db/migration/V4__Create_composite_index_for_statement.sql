-- V4: TẠO COMPOSITE INDEX ĐỂ TỐI ƯU HÓA TRUY VẤN SAO KÊ THEO THÁNG
-- Lịch sử: Truy vấn COUNT(*) theo (from_account, created_at) bị quá tải I/O (Disk Reads) 
-- do Single Index trả về quá nhiều dữ liệu rác không thỏa mãn điều kiện Date.
-- Giải pháp: Thêm Composite Index gộp (Account + Date) để hỗ trợ Multi-column filtering.

CREATE INDEX idx_transfer_account_date ON transfers(from_account, created_at);
