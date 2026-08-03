# 07 - Transaction In Microservice

## Hoc truoc khi code

Trong monolith, `@Transactional` co the bao nhieu repository cung mot DB. Trong microservice, transfer-service va account-service co DB rieng nen khong the dung mot local transaction bao tat ca.

## Cau nho phong van

Distributed transaction kho hon local transaction. Can dung idempotency, retry, outbox hoac saga de xu ly consistency giua cac service.
