# 09 - Redis Cache Rate Limit

## Hoc truoc khi code

Redis la datastore tren RAM, phu hop cache du lieu doc nhieu va luu du lieu tam co TTL.

## Trong project co the dung cho

```text
rate limit login/transfer
cache permission
blacklist token
idempotency key TTL
```

Khong dung Redis lam nguon quyet dinh balance.
