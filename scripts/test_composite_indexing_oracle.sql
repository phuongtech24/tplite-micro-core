-- KỊCH BẢN TỐI THƯỢNG CHỨNG MINH COMPOSITE INDEX ĐÁNH BẠI SINGLE INDEX
-- Trả lời câu hỏi: Khi nào Single Index trở nên "phế vật" và phải nhờ đến Composite?

ALTER SESSION SET CURRENT_SCHEMA = TRANSFER_DB;

-- ==========================================
-- BƯỚC 1: XÓA SẠCH SÀNH SANH INDEX ĐỂ LÀM LẠI TỪ ĐẦU
-- Bôi đen chạy từng dòng dưới đây (Báo lỗi "không tồn tại" thì kệ nó)
-- ==========================================
DROP INDEX idx_transfers_from_account;
DROP INDEX idx_transfers_to_account;
DROP INDEX idx_transfer_account_date;

-- ==========================================
-- BƯỚC 2: TẠO RA 1 TÀI KHOẢN "SIÊU ĐẠI GIA" (SUPER MERCHANT)
-- Giả sử tài khoản ví điện tử 'MOMO_PAY' chiếm tới 5 TRIỆU giao dịch (Một nửa bảng).
-- Bôi đen chạy cụm lệnh UPDATE này (Đợi khoảng 20-40s):
-- ==========================================
UPDATE transfers 
SET from_account = 'MOMO_PAY' 
WHERE ROWNUM <= 5000000;

COMMIT;

-- Kiểm tra xem MOMO_PAY có đủ 5 triệu dòng chưa:
SELECT count(*) FROM transfers WHERE from_account = 'MOMO_PAY';

-- ==========================================
-- BƯỚC 3: TẠO SINGLE INDEX VÀ XEM NÓ BỊ "HÀNH SÁP MẶT"
-- ==========================================
-- Chúng ta tạo lại Single Index chỉ cho 1 cột Account (Giống bài cũ)
CREATE INDEX idx_transfers_from_account ON transfers(from_account);

-- CHẠY TRUY VẤN THỬ (Đo thời gian mili-giây):
-- Yêu cầu: Lấy toàn bộ giao dịch của MOMO_PAY nhưng CHỈ TRONG THÁNG 1.
-- 🔥 BẠN HÃY CHẠY VÀ CẢM NHẬN SỰ CHẬM CHẠP KINH HOÀNG (Mất chục giây đến treo máy)!
-- Lý do: Single Index lôi lên 5 TRIỆU bản ghi MOMO_PAY, bắt ổ cứng phải quét thủ công
-- 5 triệu lần để xem cái nào thuộc tháng 1. Single Index chính thức VÔ DỤNG!
SELECT count(*) FROM transfers 
WHERE from_account = 'MOMO_PAY' 
  AND created_at BETWEEN TO_TIMESTAMP('2026-01-01', 'YYYY-MM-DD') AND TO_TIMESTAMP('2026-01-31', 'YYYY-MM-DD');

-- ==========================================
-- BƯỚC 4: TẠO COMPOSITE INDEX VÀ XEM PHÉP MÀU
-- Gộp cả (Tài khoản, Thời gian) vào làm 1. (Đợi tạo hơi lâu chút xíu)
-- ==========================================
CREATE INDEX idx_transfer_account_date ON transfers(from_account, created_at);

-- CHẠY LẠI TRUY VẤN Ở BƯỚC 3 (Đo thời gian mili-giây):
-- Lần này nó phi thẳng vào Tháng 1 của MOMO_PAY và vác kết quả ra ngay!
-- 🔥 Tốc độ sẽ giảm từ CHỤC GIÂY rớt xuống vài CHỤC MILI-GIÂY!
SELECT count(*) FROM transfers 
WHERE from_account = 'MOMO_PAY' 
  AND created_at BETWEEN TO_TIMESTAMP('2026-01-01', 'YYYY-MM-DD') AND TO_TIMESTAMP('2026-01-31', 'YYYY-MM-DD');
