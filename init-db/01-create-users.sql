-- Script tự động tạo User/Schema khi Oracle DB khởi động
ALTER SESSION SET CONTAINER = FREEPDB1;

-- Tạo User cho Identity Service
CREATE USER identity_db IDENTIFIED BY password;
GRANT CONNECT, RESOURCE, DBA TO identity_db;

-- Tạo User cho Account Service
CREATE USER account_db IDENTIFIED BY password;
GRANT CONNECT, RESOURCE, DBA TO account_db;

-- Tạo User cho Transfer Service
CREATE USER transfer_db IDENTIFIED BY password;
GRANT CONNECT, RESOURCE, DBA TO transfer_db;

-- Lưu ý: Quyền DBA cấp ở đây chỉ dùng cho môi trường dev local để Hibernate tự do tạo bảng, trình diễn. 
-- Ở Production, ngân hàng sẽ thu hồi quyền DBA và chỉ cấp quota dung lượng.
