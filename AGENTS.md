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
API Gateway Global JWT Filter (Trạm kiểm soát vé tập trung)
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

### Phase 6 - API Gateway Global JWT Filter (Trạm kiểm soát vé)

Hoc:

```text
Spring Cloud Gateway Global Filter
Boc tach JWT o Gateway thay vi tung Service
Bao mat tap trung
```

Output:

```text
Gateway tu dong chan request khong co token (tra ve 401)
Cac service ben trong duoc go bo code kiem tra Token (giam tai)
```

### Phase 7 - Event-Driven Architecture (Kafka & Outbox Pattern)

Hoc:

```text
Message Broker la gi? Apache Kafka
Giao tiep bat dong bo (Asynchronous)
Transactional Outbox Pattern de chong mat event
```

Output:

```text
Transfer-service gui event vao Kafka sau khi chuyen tien
Notification-service lang nghe event va in ra log gui Email
Tra response cho user nhanh gap doi
```

### Phase 8 - SAGA Pattern (Distributed Transaction Nang Cao)

Hoc:

```text
SAGA Choreography vs Orchestration
Compensating Transaction (Giao dich bu tru / Hoan tien)
```

Output:

```text
Bo luong goi HTTP dong bo o Phase 5
Dung Kafka de tru tien -> cong tien. Neu cong tien bi loi, tu dong ban event hoan tien ve lai cho nguoi gui.
Dam bao Eventual Consistency (Nhat quan cuoi)
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

## Nhung kien thuc mo rong (Tu cac repo Bank Core tren Github)

Dua tren viec khao sat cac du an Core Banking tieu chuan tren Github (nhu BankSystemMicroservices, Online Banking Microservices), duoi day la danh sach cac kien thuc va kien truc nang cao ban co the ap dung cho du an nay sau khi da hoan thanh V1:

1. **Event-Driven Architecture (EDA) & Message Broker**:
   - Su dung Apache Kafka hoac RabbitMQ de cac service giao tiep bat dong bo (Asynchronous).
   - Vi du: Khi TransferService chuyen tien xong, no ban ra mot su kien `TransferCompletedEvent`, NotificationService se lang nghe va gui Email/SMS ma khong lam nghen request cua User.

2. **Distributed Transaction (Giao dich phan tan) voi SAGA Pattern**:
   - Giai quyet bai toan: Lam sao de commit hoac rollback giao dich khi no lien quan den nhieu Service khac nhau (vi du: tru tien o AccountService A, cong tien o AccountService B nhung mang bi loi giua chung).
   - Ket hop voi Kafka Outbox Pattern de dam bao khong bao gio mat data (Eventual Consistency).

3. **CQRS (Command Query Responsibility Segregation)**:
   - Tach biet hoan toan logic Ghi (Command) va logic Doc (Query).
   - Ghi data vao MariaDB/PostgreSQL, sau do dong bo data sang Elasticsearch hoac MongoDB de toi uu toc do doc/tim kiem lich su giao dich.

4. **Observability & Distributed Tracing (Giam sat he thong)**:
   - Khi co mot Request bi loi 500, lam sao biet no dang chet o Gateway, Identity hay Account service?
   - Tich hop **Zipkin/Jaeger** (Trace Request), **Prometheus & Grafana** (Ve bieu do Monitor CPU/RAM/Request cua tung service), va **ELK Stack** (Gom log tap trung).

5. **Clean Architecture & Domain-Driven Design (DDD)**:
   - Chia cau truc code thanh cac lop tach biet (Domain, Application, Infrastructure, Presentation) de dam bao code loi (Loi ngan hang) khong bi phu thuoc vao bat ky Framework nao.

6. **Identity Provider (SSO)**:
   - Thay the viec tu build Identity Service bang cach tich hop **Keycloak** hoac **OAuth2/OpenID Connect**, giup ho tro xac thuc 2 lop (2FA), Social Login.
