# 🏠 RentFlow – Rental Management SaaS

RentFlow is a real-world Rental Management SaaS application built using Spring Boot. It helps property owners manage rental properties, tenants, and authentication securely using JWT-based authentication.

This project is designed following industry-standard architecture and best practices to demonstrate enterprise-level backend development.

---

# 🚀 Features

## ✅ Authentication & Authorization

- User Registration
- User Login
- JWT Token Generation
- JWT Token Validation
- Spring Security Integration
- BCrypt Password Encryption
- Stateless Authentication
- Role-Based Authorization (OWNER, ADMIN)

---

## ✅ Property Management

Property owners can:

- Create Property
- View Their Properties
- Get Property by ID
- Update Property Details
- Delete Property
- Owner Authorization
- Property Status Management

Property Status

- AVAILABLE
- RENTED
- UNDER_MAINTENANCE

---

## ✅ Tenant Management

Property owners can:

- Add Tenant
- View All Their Tenants
- View Tenant Details
- Update Tenant Information
- Soft Delete (Deactivate Tenant)

Business Rules

- One property can have only one ACTIVE tenant.
- When tenant is created
  - Property Status → RENTED
- When tenant is deactivated
  - Property Status → AVAILABLE

Tenant Status

- ACTIVE
- INACTIVE

---

# 🔐 Security

Implemented using Spring Security + JWT.

Authentication Flow

```
Client
    │
    ▼
Login API
    │
    ▼
AuthenticationManager
    │
    ▼
UserDetailsService
    │
    ▼
JWT Token Generated
    │
    ▼
Client stores Token
    │
    ▼
Authorization: Bearer JWT_TOKEN
    │
    ▼
JWT Filter
    │
    ▼
Protected APIs
```

---

# 🏗 Project Architecture

```
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
Database
```

Project follows layered architecture.

```
controller
service
repository
entity
dto
config
security
exception
response
constant
enums
```

---

# 🛠 Tech Stack

- Java 17
- Spring Boot 2.7
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- ModelMapper
- Eclipse IDE
- Postman
- Git
- GitHub

---

# 📁 Modules Completed

## Module 1

Authentication

✔ Register

✔ Login

✔ JWT

✔ BCrypt

✔ Spring Security

✔ Role Authorization

---

## Module 2

Property Management

✔ Create Property

✔ Get Property

✔ Get My Properties

✔ Update Property

✔ Delete Property

✔ Owner Validation

---

## Module 3

Tenant Management

✔ Create Tenant

✔ Update Tenant

✔ Get Tenant

✔ Get All Tenants

✔ Soft Delete

✔ Business Validation

✔ Property Status Synchronization

---

# 📂 Database Design

## User

```
id
firstName
lastName
email
password
phoneNumber
role
```

---

## Role

```
id
name
```

---

## Property

```
id
propertyName
description
propertyType

addressLine1
addressLine2
city
state
country
postalCode

monthlyRent
securityDeposit
maintenanceCharge

bedrooms
bathrooms
area

parkingAvailable
balconyAvailable

status

owner_id
```

---

## Tenant

```
id
firstName
lastName
email
phoneNumber

aadhaarNumber

occupation

startDate
endDate

status

property_id
```

---

# 🔑 Authentication APIs

## Register

```
POST /api/auth/register
```

---

## Login

```
POST /api/auth/login
```

Returns JWT Token.

---

# 🏠 Property APIs

## Create Property

```
POST /api/properties
```

---

## Get My Properties

```
GET /api/properties
```

---

## Get Property

```
GET /api/properties/{id}
```

---

## Update Property

```
PUT /api/properties/{id}
```

---

## Delete Property

```
DELETE /api/properties/{id}
```

---

# 👨‍💼 Tenant APIs

## Create Tenant

```
POST /api/tenants
```

---

## Get All Tenants

```
GET /api/tenants
```

---

## Get Tenant

```
GET /api/tenants/{id}
```

---

## Update Tenant

```
PUT /api/tenants/{id}
```

---

## Deactivate Tenant

```
PATCH /api/tenants/{id}/deactivate
```

---

# ⚙ Business Rules

✔ Only authenticated users can access protected APIs.

✔ Passwords are encrypted using BCrypt.

✔ JWT is validated on every request.

✔ Property owner can access only their own properties.

✔ Property owner can manage only their own tenants.

✔ One ACTIVE tenant per property.

✔ Soft Delete implemented using Tenant Status.

✔ Property status changes automatically based on tenant activity.

---

# ❗ Exception Handling

Global Exception Handling implemented using `@RestControllerAdvice`.

Handles:

- Resource Not Found
- Validation Errors
- Duplicate Resources
- Unauthorized Access
- Access Denied
- Bad Request
- Internal Server Error

---

# 📌 Future Modules

- Lease Management
- Rent Collection
- Payment History
- Maintenance Requests
- Dashboard
- Search & Pagination
- File Upload
- Email Notifications
- Swagger Documentation
- Unit Testing
- Docker
- Deployment (AWS/Render)

---

# 📸 Testing

All APIs have been tested using Postman.

- Authentication
- Property Management
- Tenant Management

---

# 📈 Current Project Status

| Module | Status |
|---------|--------|
| Authentication | ✅ Completed |
| JWT Security | ✅ Completed |
| Property Management | ✅ Completed |
| Tenant Management | ✅ Completed |
| Lease Management | ⏳ Next |
| Payment Module | ⏳ Planned |
| Dashboard | ⏳ Planned |

---

# 👨‍💻 Author

**Pratik Katkar**

Backend Java Developer

### Tech Skills

- Java
- Spring Boot
- Spring Security
- JWT
- Hibernate
- MySQL
- REST APIs
- Maven
- Git & GitHub

---

## ⭐ If you like this project, don't forget to star the repository!
