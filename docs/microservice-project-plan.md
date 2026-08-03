# TPLite Retail Banking Microservices

Du an hoc microservice tu Bank_Core monolith, muc tieu la vua dung vua hoc, khong copy tutorial.

## Muc tieu

- Tach nghiep vu retail banking thanh 5 service nho.
- Moi service co bien gioi ro rang, database rieng.
- Hoc lan luot: API Gateway, Service Discovery, JWT, Database per Service, HTTP communication, distributed transaction, Kafka Outbox, Redis, observability.
- Moi phan moi deu co learning note truoc khi code.

## Service chinh

```text
identity-service       -> auth, JWT, user/role/permission
customer-service       -> customer profile, KYC
account-service        -> account, balance, hold/clear/release
transfer-service       -> transfer orchestration, idempotency, transaction history
notification-service   -> Kafka consumer, in-app notification
```

## Ha tang

```text
api-gateway            -> route, JWT filter, CORS, rate limit sau nay
discovery-server       -> Eureka service registry
common-lib             -> shared DTO/exception/util toi thieu
docker-compose.yml     -> Postgres, Kafka, Redis va cac service sau nay
```

## Rule hoc moi phase

```text
1. Hoc tong quan
2. Ve flow request/data
3. Code ban nho nhat chay duoc
4. Test bang Postman/Docker
5. Ghi cau tra loi phong van
```

## Phases

### Phase 1 - Skeleton

Dung parent project, cac service folder, gateway, discovery, docker compose.

### Phase 2 - Identity + Gateway JWT

Register/login, JWT, gateway filter, 401/403.

### Phase 3 - Customer + Account

Customer profile, KYC, account, balance.

### Phase 4 - Transfer v1

Transfer-service goi account-service bang HTTP, idempotency, lock/hold/clear trong account-service.

### Phase 5 - Kafka Notification + Outbox

Transfer tao notification event, notification-service consume Kafka.

### Phase 6 - Redis + Observability

Rate limit, cache permission/config, requestId/correlationId, actuator.

## Postman flow can demo

```text
1. Register/login
2. Create customer profile
3. Submit KYC
4. Review KYC
5. Create account
6. Transfer money
7. Retry same transfer with same Idempotency-Key
8. Check transaction history
9. Check notification
10. Stop Kafka de xem retry/outbox
```
