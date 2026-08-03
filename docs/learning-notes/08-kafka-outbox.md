# 08 - Kafka Outbox

## Hoc truoc khi code

Outbox Pattern la luu event vao DB cung transaction nghiep vu, roi worker gui Kafka sau.

## Cau nho phong van

Outbox giup tranh mat event khi DB commit thanh cong nhung Kafka loi. Neu publish loi thi worker retry sau, khong lam hong flow chinh.
