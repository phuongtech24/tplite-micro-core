# AGENTS.md - Learning Rules For TPLite Banking Microservices

## Muc tieu cua du an

Day la du an hoc microservice bang cach tu xay tu dau, khong copy tutorial. Muc tieu chinh la:

- Hieu ban chat microservice qua tung van de that.
- Tu code tung phan nho, sau do review va sua.
- Moi cong nghe moi phai co note giai thich truoc khi code.
- Moi tinh nang phai test duoc bang lenh hoac Postman.
- Sau moi phan phai co cau tra loi phong van ngan gon.

## Nguyen tac lam viec voi AI/Codex

AI/Codex khong mac dinh code thay toan bo. Thu tu uu tien:

1. Giai thich kien thuc nen tang.
2. Ve luong request/data flow.
3. Goi y file can tao va noi dung can code.
4. De user tu code truoc.
5. Review code user vua viet.
6. Chi sua/code truc tiep khi user yeu cau ro: "code giup t", "sua giup t", "implement".

Neu user dang hoc mot phan moi, AI phai tra loi theo format:

```text
1. No la gi?
2. Vi sao can?
3. Neu khong co thi loi gi?
4. Trong project nay nam o dau?
5. Can tao/sua file nao?
6. Test nhu the nao?
7. Cau tra loi phong van ngan gon
```

## Khong duoc lam

- Khong nhay vao code khi user dang hoi kien thuc.
- Khong them nhieu cong nghe mot luc.
- Khong tach qua nhieu service ngay tu dau.
- Khong viet code phuc tap hon muc can hoc o phase hien tai.
- Khong claim project co tinh nang neu chua co code/test that.
- Khong copy-paste tutorial thanh code ma khong giai thich.

## V1 scope nho de hoc chac

V1 chi tap trung cac module sau:

```text
discovery-server
api-gateway
identity-service
account-service
transfer-service
```

Tam de phase sau:

```text
config-server
customer-service
notification-service
Kafka Outbox
Redis
Loan/Card/Audit
```

Ly do: v1 can hoc chac gateway, discovery, JWT, service-to-service HTTP va distributed transaction truoc.

## Learning workflow moi phase

Moi phase phai co 5 output:

```text
1. Learning note trong docs/learning-notes
2. Flow diagram bang text/mui ten
3. Code nho nhat chay duoc
4. Test command/Postman request
5. Cau tra loi phong van
```

## Phase order

### Phase 1 - Discovery Server

Hoc:

```text
Service Discovery la gi?
Eureka la gi?
Viec service dang ky len registry co tac dung gi?
```

Output:

```text
discovery-server chay port 8761
Mo duoc Eureka UI: http://localhost:8761
```

### Phase 2 - API Gateway

Hoc:

```text
API Gateway la gi?
Routing la gi?
Gateway khac business service nhu the nao?
```

Output:

```text
api-gateway chay port 8080
Route duoc request vao service mau
```

### Phase 3 - Identity Service

Hoc:

```text
Authentication vs Authorization
JWT stateless
Access token vs refresh token
Spring Security filter chain
```

Output:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET /api/v1/me
```

### Phase 4 - Account Service

Hoc:

```text
Account balance
Hold/Clear/Release
Pessimistic lock
READ COMMITTED
```

Output:

```text
POST /api/v1/accounts
GET /api/v1/accounts/my
Internal API hold/clear/credit
```

### Phase 5 - Transfer Service

Hoc:

```text
Service-to-service HTTP
Idempotency-Key
Distributed transaction problem
Retry va failure handling co ban
```

Output:

```text
POST /api/v1/transfers
GET /api/v1/transactions/my
Transfer-service goi account-service
Cung Idempotency-Key khong tru tien 2 lan
```

## Cach hoi AI de hoc tot

Nen hoi:

```text
Giai thich discovery-server truoc khi code
Chi t can tao file nao cho discovery-server
Review code discovery-server cua t
Cho t Postman/test command de kiem tra
Viet cau tra loi phong van cho phan nay
```

Khong nen hoi chung chung:

```text
Lam het microservice cho t
Code full du an
Them tat ca cong nghe vao luon
```

## Definition of Done cho moi phan

Mot phan chi duoc coi la xong khi:

```text
Code compile duoc
App start duoc
Endpoint/health check test duoc
Co note giai thich
User tra loi duoc cau phong van co ban
```

## Cau kim chi nam

Hoc microservice khong phai la them that nhieu cong nghe, ma la hieu tung van de phat sinh khi tach monolith thanh nhieu service: routing, service discovery, config, security, network failure, transaction consistency, messaging, cache va observability.
