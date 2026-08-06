-- KỊCH BẢN KIỂM THỬ HIỆU NĂNG DATABASE INDEXING TRÊN ORACLE (DÀNH CHO CV)
-- Hướng dẫn: Mở DBeaver, kết nối vào Oracle, đảm bảo đang ở Schema TRANSFER_DB.
-- Đặt con trỏ chuột vào từng khối lệnh và bấm Ctrl + Enter để chạy.

ALTER SESSION SET CURRENT_SCHEMA = TRANSFER_DB;

-- ==========================================
-- BƯỚC 1: XÓA CŨ VÀ TẠO BẢNG GIẢ LẬP
-- ==========================================
-- Chạy lệnh này nếu bảng đã tồn tại từ trước để xóa đi làm lại từ đầu
-- DROP TABLE transfers;

CREATE TABLE transfers (
    id RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
    from_account VARCHAR2(255),
    to_account VARCHAR2(255),
    amount NUMBER(19, 2),
    status VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- BƯỚC 2: BƠM 10 TRIỆU BẢN GHI BẰNG PL/SQL (Chạy mất khoảng 3-5 phút)
-- Mẹo: Cứ để đó cho nó chạy xong 100 vòng.
-- ==========================================
DECLARE
    TYPE t_transfer IS TABLE OF transfers%ROWTYPE;
    v_transfers t_transfer := t_transfer();
BEGIN
    FOR i IN 1..100 LOOP -- Chạy 100 vòng, mỗi vòng 100.000 dòng = 10 triệu dòng
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

-- Kiểm tra xem đã đủ 10 triệu dòng chưa
SELECT count(*) FROM transfers;

-- ==========================================
-- BƯỚC 3: TRUY VẤN KHI CHƯA CÓ INDEX (FULL TABLE SCAN)
-- ==========================================
EXPLAIN PLAN FOR
SELECT * FROM transfers 
WHERE from_account = '100050' OR to_account = '100050'
ORDER BY created_at DESC
FETCH FIRST 50 ROWS ONLY;

-- Xem kết quả (Cost sẽ rất cao ~ 28.000)
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ==========================================
-- BƯỚC 4: TẠO INDEX ĐỂ TỐI ƯU HÓA MỆNH ĐỀ "OR"
-- Giải pháp: Tách thành 2 Index độc lập cho 2 cột. (Mất khoảng 30-50s)
-- ==========================================
CREATE INDEX idx_transfers_from_account ON transfers(from_account);
CREATE INDEX idx_transfers_to_account ON transfers(to_account);

-- ==========================================
-- BƯỚC 5: TRUY VẤN LẠI BẰNG "UNION ALL" (Tuyệt chiêu tối ưu)
-- ==========================================
EXPLAIN PLAN FOR
SELECT * FROM (
    SELECT * FROM transfers WHERE from_account = '100050'
    UNION ALL
    SELECT * FROM transfers WHERE to_account = '100050'
)
ORDER BY created_at DESC
FETCH FIRST 50 ROWS ONLY;

-- Xem kết quả (Xuất hiện INDEX RANGE SCAN, Cost lấy dữ liệu giảm còn ~3)
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ==========================================
-- BƯỚC 6: CHẠY THẬT ĐỂ ĐO MILI-GIÂY
-- Bôi đen toàn bộ lệnh dưới đây, nhìn xuống góc dưới cùng DBeaver để lấy số 0.0xx s
-- ==========================================
SELECT * FROM (
    SELECT * FROM transfers WHERE from_account = '100050'
    UNION ALL
    SELECT * FROM transfers WHERE to_account = '100050'
)
ORDER BY created_at DESC
FETCH FIRST 50 ROWS ONLY;
