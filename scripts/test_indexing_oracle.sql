-- KỊCH BẢN KIỂM THỬ HIỆU NĂNG DATABASE INDEXING TRÊN ORACLE (DÀNH CHO CV)
-- Hướng dẫn: Mở DBeaver/SQL Developer, kết nối vào Oracle, chạy từng khối lệnh.

-- ==========================================
-- BƯỚC 1: TẠO BẢNG GIẢ LẬP
-- ==========================================
CREATE TABLE transfers (
    id RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
    from_account VARCHAR2(255),
    to_account VARCHAR2(255),
    amount NUMBER(19, 2),
    status VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Xóa dữ liệu & index cũ (nếu có)
-- TRUNCATE TABLE transfers;
-- DROP INDEX idx_transfer_accounts;

-- ==========================================
-- BƯỚC 2: BƠM 10 TRIỆU BẢN GHI BẰNG PL/SQL (Chạy mất khoảng 3-5 phút)
-- ==========================================
DECLARE
    TYPE t_transfer IS TABLE OF transfers%ROWTYPE;
    v_transfers t_transfer := t_transfer();
BEGIN
    FOR i IN 1..100 LOOP -- Chạy 100 vòng, mỗi vòng 100.000 dòng = 10.000.000 dòng
        v_transfers.DELETE;
        FOR j IN 1..100000 LOOP
            v_transfers.EXTEND;
            v_transfers(j).id := SYS_GUID();
            v_transfers(j).from_account := TO_CHAR(100000 + TRUNC(DBMS_RANDOM.VALUE(0, 100)));
            v_transfers(j).to_account := TO_CHAR(100000 + TRUNC(DBMS_RANDOM.VALUE(0, 100)));
            v_transfers(j).amount := ROUND(DBMS_RANDOM.VALUE(10, 10000), 2);
            v_transfers(j).status := 'COMPLETED';
            v_transfers(j).created_at := SYSTIMESTAMP - NUMTODSINTERVAL(DBMS_RANDOM.VALUE(0, 365), 'DAY');
        END LOOP;
        
        FORALL k IN 1..v_transfers.COUNT
            INSERT INTO transfers VALUES v_transfers(k);
        
        COMMIT;
    END LOOP;
END;
/

-- Kiểm tra số lượng
-- SELECT count(*) FROM transfers;

-- ==========================================
-- BƯỚC 3: TRUY VẤN KHI CHƯA CÓ INDEX (FULL TABLE SCAN)
-- ==========================================
-- Lệnh này sẽ phân tích chiến thuật tìm kiếm của Oracle (Bạn bôi đen cả 2 dòng và chạy)
EXPLAIN PLAN FOR
SELECT * FROM transfers 
WHERE from_account = '100050' OR to_account = '100050'
ORDER BY created_at DESC
FETCH FIRST 50 ROWS ONLY;

-- Chạy lệnh này để xem kết quả phân tích (Để ý cột "Cost" và chữ "TABLE ACCESS FULL")
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ==========================================
-- BƯỚC 4: TẠO INDEX ĐỂ TỐI ƯU HÓA MỆNH ĐỀ "OR"
-- Bẫy kinh điển: Lệnh OR làm vô hiệu hóa Composite Index.
-- Giải pháp: Phải tách thành 2 Index độc lập cho 2 cột.
-- ==========================================
CREATE INDEX idx_transfer_from_account ON transfers(from_account);
CREATE INDEX idx_transfer_to_account ON transfers(to_account);

-- ==========================================
-- BƯỚC 5: TRUY VẤN LẠI BẰNG "UNION ALL" (Tuyệt chiêu tối ưu)
-- ==========================================
-- Chạy lệnh phân tích để thấy INDEX RANGE SCAN
EXPLAIN PLAN FOR
SELECT * FROM (
    SELECT * FROM transfers WHERE from_account = '100050'
    UNION ALL
    SELECT * FROM transfers WHERE to_account = '100050'
)
ORDER BY created_at DESC
FETCH FIRST 50 ROWS ONLY;

-- Xem kết quả (Cost giảm, xuất hiện INDEX RANGE SCAN)
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
