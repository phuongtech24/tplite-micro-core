# TPLite Banking Microservices - Architecture

## Standard building blocks

```text
Client
  -> api-gateway
  -> identity/customer/account/transfer/notification services
  -> database per service
  -> Kafka for async notification events
```

## Infrastructure modules

```text
config-server     -> centralized config server
config-repo       -> local config files for services
discovery-server  -> Eureka service registry
api-gateway       -> single entry point, routes, JWT filter, CORS
infra/            -> docker/kafka/postgres/redis setup later
```

## Service ownership

```text
identity-service      owns users/roles/permissions
customer-service      owns customers/KYC
account-service       owns accounts/balance
transfer-service      owns transfers/idempotency/outbox
notification-service  owns notifications
```

Rule: service khac khong join truc tiep database cua nhau.
