# 05 - Database Per Service

## Hoc truoc khi code

Moi microservice so huu database rieng. Service khac khong join truc tiep bang cua no.

## Trong project

```text
identity_db
customer_db
account_db
transfer_db
notification_db
```

## Cau nho phong van

Database per service giup tach ownership ro rang, nhung lam transaction lien service phuc tap hon. Khong con mot `@Transactional` bao duoc nhieu DB/service.
