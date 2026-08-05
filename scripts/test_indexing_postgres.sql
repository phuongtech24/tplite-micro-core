-- KỊCH BẢN KIỂM THỬ HIỆU NĂNG DATABASE INDEXING TRÊN POSTGRESQL (DÀNH CHO CV)
-- Hướng dẫn: Mở DBeaver, kết nối vào PostgreSQL, mở file này ra và bôi đen chạy từng bước một.

-- ==========================================
-- BƯỚC 1: TẠO BẢNG GIẢ LẬP (NẾU CHƯA CÓ)
-- ==========================================
CREATE TABLE IF NOT EXISTS transfer (
    id UUID PRIMARY KEY,
    from_account VARCHAR(255),
    to_account VARCHAR(255),
    amount DECIMAL(19, 2),
    status VARCHAR(50),
    created_at TIMESTAMP
);

-- Xóa dữ liệu cũ (nếu có) để test lại từ đầu
TRUNCATE TABLE transfer;
DROP INDEX IF EXISTS idx_transfer_accounts;

-- ==========================================
-- BƯỚC 2: BƠM 10 TRIỆU BẢN GHI VÀO DB (Khoảng 20-30 giây)
-- Đừng lo, PostgreSQL xử lý hàm generate_series này cực kỳ nhẹ nhàng, không sập máy đâu!
-- ==========================================
INSERT INTO transfer (id, from_account, to_account, amount, status, created_at)
SELECT 
    gen_random_uuid(), 
    -- Sinh ngẫu nhiên tài khoản từ 100000 đến 100100 (Để dễ trúng khi query)
    (100000 + floor(random() * 100))::text, 
    (100000 + floor(random() * 100))::text, 
    (random() * 10000)::numeric(19,2),
    'COMPLETED',
    now() - (random() * 365 * interval '1 day')
FROM generate_series(1, 10000000); 

-- Kiểm tra lại xem đủ 10 triệu chưa:
-- SELECT count(*) FROM transfer;


-- ==========================================
-- BƯỚC 3: TRUY VẤN KHI **CHƯA CÓ INDEX** (FULL TABLE SCAN)
-- Hãy chạy lệnh EXPLAIN ANALYZE này, để ý dòng "Execution Time: ..." ở kết quả trả về (thường > 2-3 giây)
-- ==========================================
EXPLAIN ANALYZE
SELECT * FROM transfer 
WHERE from_account = '100050' OR to_account = '100050'
ORDER BY created_at DESC
LIMIT 50;


-- ==========================================
-- BƯỚC 4: TẠO COMPOSITE INDEX B-TREE 
-- Bước này giống hệt file migration Flyway của bạn (Mất khoảng 10-15 giây để Index 10 triệu dòng)
-- ==========================================
CREATE INDEX idx_transfer_accounts ON transfer(from_account, to_account);


-- ==========================================
-- BƯỚC 5: TRUY VẤN LẠI KHI **ĐÃ CÓ INDEX** (INDEX SEEK)
-- Chạy lại lệnh EXPLAIN ANALYZE này, để ý dòng "Execution Time: ..." 
-- Bạn sẽ thấy nó giảm tụt quần xuống chỉ còn khoảng 10ms - 50ms (Nhanh gấp hàng trăm lần!)
-- Cầm kết quả này ghi thẳng vào CV nhé! 😎
-- ==========================================
EXPLAIN ANALYZE
SELECT * FROM transfer 
WHERE from_account = '100050' OR to_account = '100050'
ORDER BY created_at DESC
LIMIT 50;
