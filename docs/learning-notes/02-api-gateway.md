# 02 - API Gateway

## Hoc truoc khi code

API Gateway la cong vao duy nhat cua client. Client khong can biet tung service port nao, chi goi gateway.

## Trong project dung de lam gi?

```text
/api/v1/auth/**          -> identity-service
/api/v1/customers/**     -> customer-service
/api/v1/accounts/**      -> account-service
/api/v1/transfers/**     -> transfer-service
/api/v1/notifications/** -> notification-service
```

## Cau nho phong van

Gateway giup tap trung routing, CORS, auth filter va rate limit. Service ben trong chi tap trung xu ly nghiep vu.
