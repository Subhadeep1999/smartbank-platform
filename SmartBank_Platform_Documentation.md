# SmartBank Platform --- Project Documentation

## 1. Project Overview

**SmartBank Platform** is an enterprise-style banking backend project
built as a portfolio/project implementation to demonstrate practical
experience with:

-   Java 21
-   Spring Boot
-   Spring MVC / REST APIs
-   Spring Data JPA
-   Hibernate
-   PostgreSQL
-   Spring Security
-   JWT authentication
-   Spring Cloud Gateway
-   Microservices
-   Resilience4j
-   Validation
-   Actuator
-   Role-based and permission-based authorization
-   Java OOP and clean service boundaries
-   Repository/service/controller architecture
-   Database initialization and seed data

The project is being developed incrementally, with authentication and
authorization implemented centrally through an **Auth Service + API
Gateway**.

------------------------------------------------------------------------

# 2. Current Microservice Architecture

The current project follows a microservices-oriented structure:

``` text
                         ┌──────────────────────┐
                         │       Bruno          │
                         │   API Testing Client  │
                         └──────────┬───────────┘
                                    │
                                    │ HTTP :8080
                                    ▼
                         ┌──────────────────────┐
                         │     API Gateway      │
                         │      Port 8080       │
                         │                      │
                         │ Spring Cloud Gateway │
                         │ Spring Security       │
                         │ JWT Validation        │
                         │ Authorization         │
                         └───────┬───────┬──────┘
                                 │       │
                    ┌────────────┘       └──────────────┐
                    │                                   │
                    ▼                                   ▼
          ┌──────────────────┐                ┌──────────────────┐
          │  Customer        │                │    Account       │
          │  Service         │                │    Service       │
          │                  │                │                  │
          │ REST + JPA       │                │ REST + JPA       │
          │ PostgreSQL       │                │ PostgreSQL       │
          └────────┬─────────┘                └────────┬─────────┘
                   │                                   │
                   │                                   │
                   └───────────────┬───────────────────┘
                                   │
                                   ▼
                          ┌──────────────────┐
                          │   PostgreSQL     │
                          │                  │
                          │ auth_schema      │
                          │ customer schema  │
                          │ account schema   │
                          └──────────────────┘

                         ┌──────────────────┐
                         │    Auth Service  │
                         │     Port 8083    │
                         │                  │
                         │ Login             │
                         │ User/Role data    │
                         │ Permissions       │
                         │ JWT generation    │
                         └──────────────────┘
```

> The project deliberately does **not** use a server-side login session.
> Authentication is stateless and based on JWT.

------------------------------------------------------------------------

# 3. Services and Responsibilities

## 3.1 Auth Service

The Auth Service is responsible for:

-   User registration
-   Login
-   Password authentication
-   User status
-   Role management
-   Permission management
-   User-role mapping
-   Role-permission mapping
-   JWT generation
-   Authentication-related exceptions
-   Initial authorization data

Current development port:

``` text
8083
```

The service uses Spring Security and Spring Data JPA.

------------------------------------------------------------------------

## 3.2 API Gateway

The API Gateway is the single entry point for protected APIs.

Current development port:

``` text
8080
```

Responsibilities:

-   Route requests to downstream services
-   Validate JWTs
-   Extract role and permissions from JWT
-   Convert JWT claims into Spring Security authorities
-   Enforce permission-based authorization
-   Return consistent 401/403 security responses
-   Provide centralized security configuration

The Gateway uses Spring Cloud Gateway WebFlux and therefore uses
reactive Spring Security configuration.

------------------------------------------------------------------------

## 3.3 Customer Service

Responsibilities currently implemented:

``` text
POST   /api/v1/customers
GET    /api/v1/customers/{id}
GET    /api/v1/customers/cif/{cifId}
GET    /api/v1/customers
PUT    /api/v1/customers/{customerId}
PATCH  /api/v1/customers/{customerId}/deactivate
```

The service uses:

-   Spring Boot
-   Spring MVC
-   Spring Data JPA
-   Hibernate
-   PostgreSQL
-   Validation
-   Resilience4j for downstream calls where applicable

------------------------------------------------------------------------

## 3.4 Account Service

Responsibilities currently implemented:

``` text
POST /api/v1/accounts
GET  /api/v1/accounts/{accountNumber}
```

Account creation validates the customer relationship through the
Customer Service.

The Account Service uses:

-   Spring Boot
-   Spring MVC
-   Spring Data JPA
-   Hibernate
-   PostgreSQL
-   Validation
-   Resilience4j
-   REST client communication with Customer Service

------------------------------------------------------------------------

# 4. Authentication Architecture

## 4.1 Login Flow

The login flow is:

``` text
Client
  │
  │ POST /api/v1/auth/login
  ▼
Auth Service
  │
  ├── Authenticate username/password
  │
  ├── Load User
  │
  ├── Load active UserRole
  │
  ├── Load Role
  │
  ├── Load Role Permissions
  │
  └── Generate JWT
  │
  ▼
JWT returned to client
```

The client then sends:

``` http
Authorization: Bearer <JWT>
```

for protected APIs.

------------------------------------------------------------------------

# 5. Stateless Authentication

We explicitly decided **not to use server-side sessions**.

The project does not create a login session ID and store it for
subsequent service calls.

Instead:

``` text
Login
  ↓
JWT generated
  ↓
Client stores JWT
  ↓
JWT sent with every protected request
  ↓
Gateway validates JWT
```

This keeps the authentication model stateless and is more appropriate
for a microservices/API architecture.

------------------------------------------------------------------------

# 6. JWT Design

The JWT contains identity and authorization information.

The project currently uses claims such as:

``` text
userId
username
bankId
role
permissions
```

Conceptually:

``` json
{
  "userId": "USR00000001",
  "username": "teller01",
  "bankId": "SB001",
  "role": "TELLER",
  "permissions": [
    "ACCOUNT_CREATE",
    "ACCOUNT_READ",
    "CUSTOMER_READ"
  ]
}
```

The exact token structure should always be treated according to the
current Auth Service implementation.

------------------------------------------------------------------------

# 7. JWT Validation in API Gateway

The Gateway is configured as an OAuth2 Resource Server.

The Gateway validates:

-   JWT signature
-   Token validity
-   Expiration
-   Authentication state

An expired or invalid JWT results in:

``` text
401 Unauthorized
```

Requests without authentication are also rejected with:

``` text
401 Unauthorized
```

------------------------------------------------------------------------

# 8. JWT → Spring Security Authorities

A custom JWT converter was added in the API Gateway.

The converter transforms:

``` text
role
permissions
```

into Spring Security authorities.

For example:

``` text
role = TELLER

permissions =
    ACCOUNT_CREATE
    ACCOUNT_READ
    CUSTOMER_READ
```

becomes:

``` text
ROLE_TELLER
ACCOUNT_CREATE
ACCOUNT_READ
CUSTOMER_READ
```

The Gateway then uses:

``` java
.hasAuthority("ACCOUNT_CREATE")
```

rather than reading raw JWT claims inside every route rule.

Because the Gateway is WebFlux-based, the synchronous custom converter
is adapted using:

``` java
ReactiveJwtAuthenticationConverterAdapter
```

------------------------------------------------------------------------

# 9. Authorization Model

The project uses:

``` text
User
  ↓
UserRole
  ↓
Role
  ↓
RolePermission
  ↓
Permission
```

The permission code is the canonical authority name.

For example:

``` text
permissions.permission_code
        ↓
JWT permissions claim
        ↓
Spring Security authority
        ↓
Gateway .hasAuthority(...)
```

Therefore:

``` text
ACCOUNT_READ
```

should be used consistently across the database, JWT, and Gateway.

We deliberately avoid using a different name such as:

``` text
ACCOUNT_VIEW
```

because that would create a mismatch between the database permission and
the Gateway authorization rule.

------------------------------------------------------------------------

# 10. Authorization Database Model

## User

The User entity contains information such as:

``` text
id
user_id
username
password_hash
bank_id
customer_cif
status
created_at
updated_at
version
```

Employee users such as tellers can have a null customer CIF.

------------------------------------------------------------------------

## Role

Roles currently include:

``` text
CUSTOMER
TELLER
BRANCH_MANAGER
BANK_ADMIN
AUDITOR
```

The role code is represented by `RoleType`.

------------------------------------------------------------------------

## Permission

Permission contains:

``` text
id
permission_code
permission_name
description
status
created_at
updated_at
version
```

`permission_code` is unique and is the canonical authorization string.

------------------------------------------------------------------------

## UserRole

`user_roles` maps users to roles.

The table intentionally stores IDs:

``` text
user_id
role_id
```

rather than duplicating username or role code.

This preserves normalization.

------------------------------------------------------------------------

## RolePermission

`role_permissions` maps roles to permissions:

``` text
role_id
permission_id
```

Again, UUID foreign-key IDs are retained rather than duplicating role
codes and permission codes.

For human-readable database reporting, a JOIN or database view can be
used.

Example:

``` sql
SELECT
    r.role_code,
    r.role_name,
    p.permission_code,
    p.permission_name,
    rp.status,
    rp.assigned_by,
    rp.assigned_at
FROM auth_schema.role_permissions rp
JOIN auth_schema.roles r
    ON r.id = rp.role_id
JOIN auth_schema.permissions p
    ON p.id = rp.permission_id
ORDER BY r.role_code, p.permission_code;
```

------------------------------------------------------------------------

# 11. Current Permission Model

The project initially contained seven permissions:

``` text
CUSTOMER_READ
CUSTOMER_CREATE

ACCOUNT_READ
ACCOUNT_CREATE
ACCOUNT_UPDATE

TRANSACTION_READ
TRANSACTION_CREATE
```

We then decided to add:

``` text
CUSTOMER_UPDATE
CUSTOMER_DEACTIVATE
```

The final intended permission set is:

``` text
CUSTOMER_READ
CUSTOMER_CREATE
CUSTOMER_UPDATE
CUSTOMER_DEACTIVATE

ACCOUNT_READ
ACCOUNT_CREATE
ACCOUNT_UPDATE

TRANSACTION_READ
TRANSACTION_CREATE
```

This gives the customer APIs semantically correct permissions rather
than incorrectly reusing `CUSTOMER_CREATE` for update/deactivation.

------------------------------------------------------------------------

# 12. Role → Permission Model

The current role mapping is designed around least privilege.

## CUSTOMER

``` text
ACCOUNT_READ
TRANSACTION_READ
```

## TELLER

``` text
CUSTOMER_READ
ACCOUNT_READ
ACCOUNT_CREATE
```

## BRANCH_MANAGER

``` text
CUSTOMER_READ
CUSTOMER_UPDATE
CUSTOMER_DEACTIVATE
ACCOUNT_READ
ACCOUNT_CREATE
ACCOUNT_UPDATE
```

## BANK_ADMIN

``` text
CUSTOMER_READ
CUSTOMER_CREATE
CUSTOMER_UPDATE
CUSTOMER_DEACTIVATE

ACCOUNT_READ
ACCOUNT_CREATE
ACCOUNT_UPDATE

TRANSACTION_READ
TRANSACTION_CREATE
```

## AUDITOR

``` text
CUSTOMER_READ
ACCOUNT_READ
TRANSACTION_READ
```

This mapping should be reviewed whenever business requirements change.

------------------------------------------------------------------------

# 13. Auth Data Initialization

The Auth Service contains an `AuthDataInitializer`.

It is implemented as a Spring `CommandLineRunner`.

At application startup it ensures that the required:

-   Bank
-   Roles
-   Permissions
-   Role-permission mappings

exist.

The initializer uses `find...().orElseGet(...)` patterns and checks
whether a role-permission mapping already exists before inserting it.

Therefore, normal application restarts do **not** blindly duplicate the
seed records.

The initializer is effectively idempotent for the data it manages.

Important distinction:

``` text
Restart application
    ≠
Drop database
```

Restarting the application does not remove existing data.

If the database is deleted/recreated, the initializer will recreate its
managed seed data.

------------------------------------------------------------------------

# 14. Database Seed Evolution

When a new permission is added to `AuthDataInitializer`, for example:

``` text
CUSTOMER_UPDATE
CUSTOMER_DEACTIVATE
```

the existing database does not need manual INSERT statements if the
initializer is being used.

On the next Auth Service startup:

``` text
Existing permission
    → found
    → reused

New permission
    → not found
    → created
```

The same principle applies to role-permission mappings.

After changing permissions, login again to obtain a new JWT because an
already-issued JWT will not automatically gain newly added permissions.

------------------------------------------------------------------------

# 15. API Gateway Authorization Rules

The Gateway uses the actual permission codes from the authorization
model.

Current intended rules:

``` java
// ACCOUNT APIs

.pathMatchers(
        HttpMethod.POST,
        "/api/v1/accounts"
)
.hasAuthority("ACCOUNT_CREATE")

.pathMatchers(
        HttpMethod.GET,
        "/api/v1/accounts/**"
)
.hasAuthority("ACCOUNT_READ")

.pathMatchers(
        HttpMethod.PUT,
        "/api/v1/accounts/**"
)
.hasAuthority("ACCOUNT_UPDATE")


// CUSTOMER APIs

.pathMatchers(
        HttpMethod.POST,
        "/api/v1/customers"
)
.hasAuthority("CUSTOMER_CREATE")

.pathMatchers(
        HttpMethod.GET,
        "/api/v1/customers/**"
)
.hasAuthority("CUSTOMER_READ")

.pathMatchers(
        HttpMethod.PUT,
        "/api/v1/customers/**"
)
.hasAuthority("CUSTOMER_UPDATE")

.pathMatchers(
        HttpMethod.PATCH,
        "/api/v1/customers/*/deactivate"
)
.hasAuthority("CUSTOMER_DEACTIVATE")
```

Public endpoints currently include:

``` text
/api/v1/auth/**
/actuator/health
```

All other unmatched endpoints require authentication.

------------------------------------------------------------------------

# 16. Global Security Error Handling

The Gateway has centralized security error handling.

## Missing JWT

Returns:

``` text
401 Unauthorized
```

with a JSON error response.

## Invalid JWT

Returns:

``` text
401 Unauthorized
```

## Expired JWT

Returns:

``` text
401 Unauthorized
```

## Authenticated but insufficient permission

Returns:

``` text
403 Forbidden
```

The project was explicitly tested for the fact that an expired token
receives 401.

The Gateway security entry point and access-denied handler are
configured to provide consistent JSON responses instead of relying on
the default empty response.

------------------------------------------------------------------------

# 17. Example Security Responses

## 401

Conceptually:

``` json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication is required to access this resource",
  "path": "/api/v1/accounts"
}
```

## 403

Conceptually:

``` json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to access this resource",
  "path": "/api/v1/customers"
}
```

------------------------------------------------------------------------

# 18. Account Service --- Customer Validation

When creating an account, the Account Service communicates with the
Customer Service to verify that the customer exists.

Conceptually:

``` text
Account Service
      |
      | GET customer by CIF
      ▼
Customer Service
      |
      ├── 200 → customer exists
      └── 404 → customer does not exist
```

The Account Service uses a dedicated client component for this
communication.

------------------------------------------------------------------------

# 19. Resilience4j in Account Service

The Customer Service client uses:

``` text
CircuitBreaker
Retry
Bulkhead
```

The client is configured with Resilience4j annotations.

Conceptually:

``` text
Account Service
      |
      ▼
CustomerServiceClient
      |
      ├── Retry
      ├── Circuit Breaker
      └── Bulkhead
      |
      ▼
Customer Service
```

## Circuit Breaker

Protects Account Service from repeatedly calling an unhealthy Customer
Service.

## Retry

Allows transient failures to be retried according to the configured
policy.

## Bulkhead

Limits concurrent calls and protects resources from overload.

The implementation uses the semaphore bulkhead type.

## Fallback

When Customer Service is unavailable or the bulkhead rejects the
request, the client throws:

``` text
CustomerServiceUnavailableException
```

This allows the application to return a controlled business/API error
instead of exposing low-level connection failures.

------------------------------------------------------------------------

# 20. Important Exception Handling Distinction

The microservices remain independently owned.

For example:

``` text
account-service
    account.exception.ResourceNotFoundException

customer-service
    customer.exception.ResourceNotFoundException
```

Account Service should not depend on Customer Service simply to reuse an
exception class.

This preserves service boundaries.

------------------------------------------------------------------------

# 21. Account Resource Not Found

The Account Service now supports account lookup by account number.

The service uses:

``` java
findByAccountNumber(accountNumber)
```

and throws an Account Service-specific:

``` text
ResourceNotFoundException
```

when the account does not exist.

The exception is handled by the Account Service's own global exception
handler.

------------------------------------------------------------------------

# 22. API Gateway Technology

The API Gateway uses Spring Cloud Gateway WebFlux.

Because it is WebFlux/reactive:

``` text
Reactive SecurityWebFilterChain
```

is used rather than the servlet-based security chain.

This also required adapting the custom JWT converter:

``` java
ReactiveJwtAuthenticationConverterAdapter
```

The project encountered and resolved this exact issue during
implementation.

------------------------------------------------------------------------

# 23. Security Configuration Lessons

One important issue encountered during development was a missing:

``` java
@Bean
```

on the `SecurityFilterChain`/security configuration method.

Without the security chain being registered as a Spring bean, the
intended custom security configuration was not being applied correctly.

After adding the bean configuration, the custom security behavior worked
correctly.

This is an important Spring Security configuration point.

------------------------------------------------------------------------

# 24. Spring Security Generated Password

During development, Spring Security initially displayed:

``` text
Using generated security password
```

This happened because Spring Boot's default security auto-configuration
was still being detected.

Once the project's custom security configuration was correctly
registered, the application used the intended authentication flow.

The generated development password should not be relied upon in
production.

------------------------------------------------------------------------

# 25. API Testing with Bruno

Bruno is currently being used to test the APIs.

The main testing pattern is:

### Login

``` http
POST http://localhost:8080/api/v1/auth/login
```

### Protected API

``` http
Authorization: Bearer <JWT>
```

Example:

``` http
POST http://localhost:8080/api/v1/accounts
Authorization: Bearer eyJ...
```

Without the Bearer token:

``` text
401 Unauthorized
```

With an expired JWT:

``` text
401 Unauthorized
```

With a valid JWT but insufficient permission:

``` text
403 Forbidden
```

With a valid JWT and required permission:

``` text
Request proceeds to downstream service
```

------------------------------------------------------------------------

# 26. Current Authentication/Authorization Flow

The complete flow is now:

``` text
                       LOGIN
                         │
                         ▼
                 ┌───────────────┐
                 │  Auth Service │
                 └───────┬───────┘
                         │
              username/password
                         │
                         ▼
                Authentication
                         │
                         ▼
                 User + Role
                         │
                         ▼
              Role + Permissions
                         │
                         ▼
                    Generate JWT
                         │
                         ▼
                      Client
                         │
                  Bearer Token
                         │
                         ▼
                 ┌───────────────┐
                 │ API Gateway   │
                 └───────┬───────┘
                         │
                    Validate JWT
                         │
                         ▼
               Extract authorities
                         │
                         ▼
             Permission authorization
                         │
               ┌─────────┴─────────┐
               │                   │
             401/403             Allowed
                                   │
                                   ▼
                         Downstream Service
```

------------------------------------------------------------------------

# 27. What Is NOT Being Used

The project deliberately does not currently use:

-   Server-side login sessions
-   Session IDs for service-to-service calls
-   Shared login state stored in Gateway
-   Duplicated role/permission codes in join tables
-   Direct dependency between Account Service and Customer Service
    exception classes

JWT authentication is stateless.

------------------------------------------------------------------------

# 28. Maven / Build Configuration

The project uses Maven with a multi-module/root project structure.

The root project is:

``` text
smartbank-platform
```

Child services include:

``` text
auth-service
account-service
customer-service
api-gateway
```

The project uses Spring Boot dependency management.

A key lesson during Gateway setup was that Spring Cloud Gateway
dependency versions must be compatible with the selected Spring
Boot/Spring Cloud combination.

The Gateway dependency that was used successfully is:

``` xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
    <version>5.0.1</version>
</dependency>
```

The exact dependency-management strategy should be kept consistent
across the root and child POMs; duplicate or incompatible BOM/version
declarations caused Maven resolution problems during setup.

------------------------------------------------------------------------

# 29. PostgreSQL

PostgreSQL is the primary database.

The Auth Service uses:

``` text
auth_schema
```

with tables including:

``` text
banks
users
roles
permissions
user_roles
role_permissions
```

The authorization data is relational and normalized.

------------------------------------------------------------------------

# 30. Concurrency and Data Integrity

The entities use optimistic locking through:

``` java
@Version
private Long version;
```

This is present in entities such as:

-   User
-   Role
-   Permission
-   UserRole
-   RolePermission
-   Bank

This helps protect against conflicting concurrent updates.

------------------------------------------------------------------------

# 31. Audit Information

The authorization mapping entities contain audit-oriented fields such
as:

``` text
assigned_at
assigned_by
created_at
updated_at
version
```

This provides a foundation for tracking authorization changes.

The current initializer uses:

``` text
SYSTEM
```

as the assignment source for seeded role-permission mappings.

------------------------------------------------------------------------

# 32. Database Design Principle

The project follows normalized relational design.

For example, instead of:

``` text
role_permissions

role_code
permission_code
```

the join table stores:

``` text
role_id
permission_id
```

This avoids duplication and maintains referential consistency.

For readability, use SQL JOINs or a database view.

------------------------------------------------------------------------

# 33. Development Troubleshooting History

Several issues were encountered and resolved during development.

## HTML instead of JSON

A request initially returned a Spring Security HTML login page.

Root cause:

The intended custom security configuration was not being registered
correctly.

Resolution:

Register the security chain/configuration correctly using `@Bean`.

------------------------------------------------------------------------

## 403 Forbidden after security configuration

Once the Gateway security configuration was correctly active, requests
without the required authorization were correctly rejected.

------------------------------------------------------------------------

## Empty 401 response

Missing/expired JWT initially produced:

``` text
401
```

with no useful response body.

Resolution:

Added centralized `authenticationEntryPoint` and `accessDeniedHandler`.

------------------------------------------------------------------------

## Expired JWT

Expired tokens correctly return:

``` text
401 Unauthorized
```

The OAuth2 resource server authentication entry point was explicitly
connected to the custom Gateway security error handler.

------------------------------------------------------------------------

## Reactive JWT converter compilation error

The custom converter implemented:

``` java
Converter<Jwt, AbstractAuthenticationToken>
```

but WebFlux expected a reactive converter.

Resolution:

``` java
new ReactiveJwtAuthenticationConverterAdapter(
    new JwtAuthenticationConverter()
)
```

------------------------------------------------------------------------

## Maven Resilience4j BOM issue

A Resilience4j BOM version placeholder was not being resolved correctly.

The project avoided continuing with a broken dependency-management setup
and adjusted the Maven configuration accordingly.

------------------------------------------------------------------------

## Spring Cloud Gateway dependency version

The Gateway dependency initially produced a missing-version error.

A working Gateway dependency configuration was eventually established
with:

``` text
spring-cloud-starter-gateway-server-webflux
version 5.0.1
```

------------------------------------------------------------------------

## Account Service ResourceNotFoundException

Account Service initially attempted to use a `ResourceNotFoundException`
that did not exist in that service.

Resolution:

Create the exception in Account Service rather than depending on
Customer Service.

------------------------------------------------------------------------

## Wrong ErrorResponse import

An exception handler accidentally referenced:

``` java
org.springframework.web.ErrorResponse
```

which is an abstraction/interface.

Resolution:

Use the project's own `ErrorResponse` DTO instead.

------------------------------------------------------------------------

# 34. Current Working State

At the current stage:

``` text
Auth Service                         ✅
Login                                ✅
JWT generation                       ✅
JWT validation                       ✅
JWT expiration validation            ✅
API Gateway                          ✅
Gateway routing                      ✅
Bearer authentication                ✅
401 handling                         ✅
403 handling                         ✅
JWT → authorities                    ✅
Account creation                     ✅
Account lookup                       ✅
Customer APIs                        ✅
Account → Customer validation        ✅
Resilience4j client protection       ✅
Role model                            ✅
Permission model                      ✅
Role-permission mapping              ✅
User-role mapping                     ✅
Permission seed initialization       ✅
CUSTOMER_UPDATE permission            ✅
CUSTOMER_DEACTIVATE permission       ✅
Permission-based Gateway rules       ✅
```

------------------------------------------------------------------------

# 35. Recommended Next Development Areas

The project can now move toward more advanced enterprise capabilities.

Recommended order:

## Phase 1 --- Complete authorization testing

Test:

``` text
TELLER
BRANCH_MANAGER
BANK_ADMIN
AUDITOR
CUSTOMER
```

against all protected endpoints.

Verify:

``` text
401 = unauthenticated
403 = authenticated but unauthorized
2xx = authorized
```

------------------------------------------------------------------------

## Phase 2 --- Transaction Service

Add:

``` text
transaction-service
```

with:

``` text
TRANSACTION_CREATE
TRANSACTION_READ
```

and integrate it through the Gateway.

------------------------------------------------------------------------

## Phase 3 --- Service-to-Service Security

Decide how internal service calls should be secured.

Potential approaches:

-   Propagate the user's JWT
-   Service-to-service JWT
-   Internal client credentials
-   Dedicated service identity

This should be designed separately from browser/client authentication.

------------------------------------------------------------------------

## Phase 4 --- Kafka

Introduce Apache Kafka for asynchronous banking events.

Potential events:

``` text
CustomerCreated
AccountCreated
AccountUpdated
CustomerDeactivated
TransactionCreated
```

Potential flow:

``` text
Account Service
      ↓
Kafka
      ↓
Notification / Audit / Transaction consumers
```

------------------------------------------------------------------------

## Phase 5 --- Batch Processing

Introduce Spring Batch for operations such as:

-   Daily transaction processing
-   Account reconciliation
-   Interest calculation
-   Bulk customer/account processing
-   Audit/report generation

------------------------------------------------------------------------

## Phase 6 --- Observability

Expand Actuator and introduce:

-   Correlation IDs
-   Structured logging
-   Metrics
-   Distributed tracing
-   Health checks
-   Readiness/liveness indicators

------------------------------------------------------------------------

# 36. Engineering Principles Established

The project has intentionally adopted the following principles:

### Stateless authentication

JWT rather than server-side sessions.

### Centralized edge security

API Gateway handles authentication and authorization.

### Least privilege

Users receive permissions through roles.

### Permission code as source of truth

Database permission codes are used as Spring Security authorities.

### Normalized authorization data

Join tables store IDs rather than duplicated codes.

### Microservice independence

Services own their domain exceptions and business logic.

### Resilience

Downstream calls are protected with retry, circuit breaker and bulkhead
patterns.

### Idempotent initialization

Startup seed logic checks before creating data.

### Optimistic locking

`@Version` protects concurrent entity updates.

------------------------------------------------------------------------

# 37. Important Development Rule Going Forward

Before adding a new Gateway authorization rule:

1.  Confirm the endpoint exists.
2.  Confirm the business action.
3.  Confirm a matching permission exists in the `permissions` table.
4.  Assign that permission to the appropriate roles.
5.  Ensure Auth Service includes it in the JWT.
6.  Add `.hasAuthority("PERMISSION_CODE")` to Gateway.
7.  Build all modules.
8.  Login again to get a fresh JWT.
9.  Test allowed and forbidden cases.

This prevents mismatches such as:

``` text
Database:
ACCOUNT_READ

Gateway:
ACCOUNT_VIEW
```

which would incorrectly result in 403.

------------------------------------------------------------------------

# 38. Current Architecture Summary

The project has now evolved into:

``` text
                         SmartBank Platform
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
        Auth Service       API Gateway       PostgreSQL
              │                 │
              │                 │
        JWT Generation    JWT Validation
              │                 │
        Roles/Permissions  Authorization
              │                 │
              └────────┬────────┘
                       │
              ┌────────┴────────┐
              │                 │
              ▼                 ▼
       Customer Service   Account Service
              │                 │
              │       Resilience4j Client
              │                 │
              └────────┬────────┘
                       ▼
                 PostgreSQL
```

The foundation is now ready for the next major features: complete
authorization testing, transaction processing, Kafka-based events, batch
processing, and stronger observability.
