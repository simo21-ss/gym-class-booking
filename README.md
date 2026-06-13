# Gym Class Booking

A web application for booking gym fitness classes, built for the **SoftUni Spring Fundamentals** individual project.
Members browse and book classes; trainers manage classes and trainer profiles.

> **Status:** in active development. This repository is built incrementally over several days. The domain model,
> repositories, and infrastructure are in place; web functionality is being added next.

## Tech Stack

- **Java** 17+
- **Spring Boot** 3.4.0 (Spring MVC, Spring Data JPA, Validation)
- **Thymeleaf** server-side templating
- **PostgreSQL** 16 (via Docker)
- **Maven** (with Maven Wrapper)
- **BCrypt** (`spring-security-crypto`) for password hashing
- Custom **session-based authentication** (`user_id` stored in the HTTP session)

## Domain Model

| Entity | Description | Relationships |
|--------|-------------|---------------|
| `Trainer` | Instructor who leads classes | — |
| `FitnessClass` | A scheduled class with capacity and status | `→ Trainer` |
| `Booking` | A member's reservation for a class | `→ FitnessClass`, `→ User` |
| `User` *(technical)* | Account with a role (`MEMBER` / `TRAINER`) | — |

All entities use a **UUID** primary key. Relationships are unidirectional `@ManyToOne`.

## Planned Features & Functionalities

- **Fitness class management (full CRUD)** — trainers create, edit, delete, and publish/cancel classes
- **Bookings (full CRUD)** — members book, reschedule, cancel, and view their bookings
- **Trainer management (full CRUD)** — trainers create, edit, and delete trainer profiles
- **Authentication** — register, session-based login/logout, role-based access control
- **Server-side validation** with field-level error messages and custom business exceptions

## Roles & Access

- **Guest** — register, login, and browse classes
- **Member** — book, reschedule, and cancel classes; view own bookings
- **Trainer** — manage fitness classes and trainer profiles

## Running Locally

Prerequisites: Java 17+, Docker.

```bash
# 1. Start the PostgreSQL database
docker compose up -d

# 2. Run the application
./mvnw spring-boot:run
```

The app will be available at http://localhost:8080.

## Integrations

- **PostgreSQL** relational database (Dockerized) accessed via Spring Data JPA.

## Database Configuration

Configured in `src/main/resources/application.yml`. Defaults match `docker-compose.yml`:

| Setting | Value |
|---------|-------|
| Database | `gymbooking` |
| User | `gymuser` |
| Password | `gympass` |
| Port | `5432` |
