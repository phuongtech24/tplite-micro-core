-- KỊCH BẢN KIỂM THỬ SỨC MẠNH INDEX KHI JOIN BẢNG (DÀNH CHO PHỎNG VẤN)
-- LƯU Ý QUAN TRỌNG: 
-- Trong Microservices thực tế, Account và Transfer nằm ở 2 Database khác nhau nên không JOIN được bằng SQL.
-- Ở bài test này, ta giả lập nhét bảng accounts vào chung TRANSFER_DB để demo kiến thức "Tại sao JOIN cần Index".

ALTER SESSION SET CURRENT_SCHEMA = TRANSFER_DB;

-- ==========================================
-- BƯỚC 1: TẠO BẢNG DUMMY ACCOUNTS (Giả lập 1 triệu tài khoản)
-- ==========================================
-- DROP TABLE dummy_accounts;
CREATE TABLE dummy_accounts (
    account_number VARCHAR2(255) PRIMARY KEY,
    customer_name VARCHAR2(255),
    status VARCHAR2(50)
);

-- ==========================================
-- BƯỚC 2: BƠM 1 TRIỆU TÀI KHOẢN (Chạy khoảng 30s)
-- ==========================================
DECLARE
    TYPE t_account IS TABLE OF dummy_accounts%ROWTYPE;
    v_accounts t_account := t_account();
BEGIN
    FOR i IN 1..10 LOOP -- 10 vòng x 100k = 1 triệu
        v_accounts.DELETE;
        FOR j IN 1..100000 LOOP
            v_accounts.EXTEND;
            v_accounts(j).account_number := TO_CHAR(100000 + (i-1)*100000 + j);
            v_accounts(j).customer_name := 'Khach Hang ' || v_accounts(j).account_number;
            v_accounts(j).status := 'ACTIVE';
        END LOOP;
        FORALL k IN 1..v_accounts.COUNT
            INSERT INTO dummy_accounts VALUES v_accounts(k);
        COMMIT;
    END LOOP;
END;
/

-- Kiểm tra số lượng (1.000.000 dòng)
SELECT count(*) FROM dummy_accounts;

-- ==========================================
-- BƯỚC 3: XÓA SẠCH INDEX ĐỂ THẤY SỰ ĐÁNG SỢ CỦA "HASH JOIN"
-- Bắt buộc phải xóa Index ở bài trước đi để mô phỏng hệ thống tồi tệ.
-- ==========================================
-- Bỏ comment và chạy 2 lệnh này nếu bạn đã tạo index ở bài trước:
DROP INDEX idx_transfers_to_account;
-- DROP INDEX idx_transfers_from_account;

-- ==========================================
-- BƯỚC 4: CHẠY JOIN 10 TRIỆU DÒNG VS 1 TRIỆU DÒNG (KHÔNG INDEX)
-- Bôi đen chạy lệnh Explain Plan này.
-- Bạn sẽ thấy Cost vọt lên Hàng Triệu, kèm theo chữ HASH JOIN khổng lồ!
-- ==========================================
EXPLAIN PLAN FOR
SELECT DISTINCT a.customer_name, a.account_number
FROM transfers t
JOIN dummy_accounts a ON t.from_account = a.account_number
WHERE t.to_account = '100050';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ==========================================
-- BƯỚC 5: CHẠY THỬ THẬT ĐỂ CẢM NHẬN SỰ TREO MÁY
-- Bôi đen chạy câu này, bạn sẽ thấy nó xoay chong chóng.
-- Hãy kiên nhẫn đợi 15-30 giây, hoặc bấm nút Cancel (ô vuông đỏ) nếu DBeaver treo!
-- ==========================================
/*
SELECT DISTINCT a.customer_name, a.account_number
FROM transfers t
JOIN dummy_accounts a ON t.from_account = a.account_number
WHERE t.to_account = '100050';
*/

-- ==========================================
-- BƯỚC 6: TẠO INDEX CỨU RỖI THẾ GIỚI
-- ==========================================
-- Index trên cột WHERE để móc ra giao dịch cực nhanh:
CREATE INDEX idx_transfers_to_account ON transfers(to_account);
-- (Cột khóa chính account_number của dummy_accounts đã tự động có Unique Index rồi)

-- ==========================================
-- BƯỚC 7: XEM SỰ LỘT XÁC SAU KHI CÓ INDEX (NESTED LOOPS)
-- Cost từ Hàng Triệu sẽ rớt xuống chỉ còn vài chục!
-- Thuật toán Hash Join cục súc sẽ biến thành Nested Loops nhẹ nhàng.
-- ==========================================
EXPLAIN PLAN FOR
SELECT DISTINCT a.customer_name, a.account_number
FROM transfers t
JOIN dummy_accounts a ON t.from_account = a.account_number
WHERE t.to_account = '100050';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Chạy thật để thấy nó trả về trong chớp mắt (vài chục mili-giây):
SELECT DISTINCT a.customer_name, a.account_number
FROM transfers t
JOIN dummy_accounts a ON t.from_account = a.account_number
WHERE t.to_account = '100050';
