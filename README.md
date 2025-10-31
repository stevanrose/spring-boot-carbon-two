# 🏢 Carbon Two – Office Management API

A modern **Spring Boot 3 / Java 17** REST API that demonstrates **clean architecture, testable design, and
production-grade engineering practices**.

This sample service manages `Office` entities with full CRUD operations — built to showcase best-practice patterns for
RESTful development, persistence, and testing.

---

## ✨ Features

| Capability     | Description                                                                        |
|----------------|------------------------------------------------------------------------------------|
| **Create**     | `POST /api/offices` — adds a new office with validation and unique code constraint |
| **Read (one)** | `GET /api/offices/{id}` — fetch an office by UUID (404 if missing)                 |
| **Read (all)** | `GET /api/offices?page=&size=&sort=` — pageable + sortable listing                 |
| **Update**     | `PUT /api/offices/{id}` — full update with validation and MapStruct merging        |
| **Delete**     | `DELETE /api/offices/{id}` — safe deletion with 404 / 409 error mapping            |

Each endpoint is fully documented in Swagger UI and verified by slice + integration tests.

---

## 🧱 Architecture Overview

```text
controller (REST, validation, Swagger)
   ↓
service (business logic, transactions)
   ↓
repository (Spring Data JPA)
   ↓
PostgreSQL (via Liquibase migrations)
```

- **Layered design:** clear separation of web, service, and persistence layers.
- **MapStruct:** compile-time DTO ↔ entity mapping for clean, fast transformations.
- **Liquibase:** version-controlled schema generation with enums and UUID PKs.
- **Jakarta Validation:** strong request validation via annotations.
- **Global Exception Handling:** consistent 404 / 409 / 400 responses.
- **Spring Data Pagination:** first-class pageable and sortable endpoints.
- **Test strategy:**
    - `@WebMvcTest` for controller slices (mocked service)
    - `@SpringBootTest` for end-to-end persistence validation

---

## 🧩 Tech Stack

| Category       | Tool                                          |
|----------------|-----------------------------------------------|
| **Language**   | Java 17                                       |
| **Framework**  | Spring Boot 3.3 / Spring Data JPA             |
| **Database**   | PostgreSQL 15 + Liquibase                     |
| **Mapping**    | MapStruct 1.6                                 |
| **Testing**    | JUnit 5 · MockMvc · Testcontainers (optional) |
| **Docs**       | springdoc-openapi-starter-webmvc-ui (2.6.0)   |
| **Code Style** | Google Java Format + Spotless/IDE plugin      |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL (local or Docker)
- Optional: IntelliJ IDEA + Google Java Format plugin

### Run locally

```bash
# build and run
mvn clean spring-boot:run

# or run tests
mvn verify
```

Swagger UI available at 👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🧾 Example API Usage

### Create Office

```bash
curl -X POST http://localhost:8080/api/offices   -H "Content-Type: application/json"   -d '{
        "code": "LON-01",
        "name": "London HQ",
        "address": "10 Downing Street",
        "gridRegionCode": "GB-LDN",
        "floorAreaM2": 300.0
      }'
```

✅ 201 Created → returns created resource with UUID

### List Offices

```
GET /api/offices?page=0&size=10&sort=name,asc
```

### Find One

```
GET /api/offices/{id}
```

### Update

```
PUT /api/offices/{id}
```

### Delete

```
DELETE /api/offices/{id}
```

---

## 🧠 Design Highlights

- **UUID primary keys** → globally unique, safe across environments.
- **Enum-typed columns** → data consistency and self-documenting schema.
- **Transactional service layer** → atomic updates and deletes.
- **Stable PageResponse wrapper** → predictable, documented pagination JSON.
- **OpenAPI annotations** → accurate, human-readable Swagger documentation.
- **Google Java Format** → consistent, opinionated code style.

---

## 🧪 Tests Included

| Layer       | Framework                   | Focus                                     |
|-------------|-----------------------------|-------------------------------------------|
| Controller  | `@WebMvcTest`               | request/response validation, status codes |
| Integration | `@SpringBootTest` + MockMvc | persistence, DB behavior                  |
| Unit        | JUnit + Mockito             | mapper + service logic                    |

Run all tests:

```bash
mvn test
```

---

## 🧰 Project Structure

```
src/
 ├── main/java/com/stevanrose/carbon_two/
 │     ├── office/
 │     │     ├── controller/   # REST endpoints
 │     │     ├── service/      # business logic
 │     │     ├── repository/   # JPA repositories
 │     │     ├── domain/       # JPA entities
 │     │     └── web/dto/      # request/response models + mappers
 │     └── common/             # shared errors, paging, etc.
 └── test/java/...             # slice + integration tests
```

---

## 🧩 Roadmap / Next Steps

- Add `Employee` and `EnergyStatement` entities (extend CRUD pattern).
- Introduce DTO validation groups for partial updates.
- Add JWT auth layer (Spring Security 6).
- Integrate Docker Compose for local DB.
- Deploy via GitHub Actions + Render or Fly.io.

---

## 🧑‍💻 Author

**[Stevan Rose](https://www.linkedin.com/in/stevanrose/)**  
Agile Technical Lead · Java / Spring / Cloud Engineer
> This project was built as a portfolio example to demonstrate production-grade code quality, test coverage, and
> architectural clarity in a modern Spring Boot application.

---

## 🪶 License

MIT © 2025 Stevan Rose
