# Gym Class Booking

A web application for booking gym fitness classes, built for the **SoftUni Spring Fundamentals** individual project.
Members browse and book classes; trainers manage classes and trainer profiles.

## Tech Stack

- **Java** 21 (LTS) — the latest version supported by Spring Boot 3.4.0
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

## Features & Functionalities

The four+ valid domain functionalities (each is user-triggered, hits a POST/PUT/DELETE endpoint, and shows a visible result):

- **Fitness class management (full CRUD)** — trainers create, edit, delete, and publish/cancel classes
- **Bookings (full CRUD)** — members book, reschedule, cancel, and view their own bookings, with capacity, duplicate, and past-class rules enforced
- **Trainer management (full CRUD)** — trainers create, edit, and delete trainer profiles (deletion is blocked while a trainer still has classes)
- **Class publish/cancel** — trainers toggle a class status

Plus supporting behaviour:

- **Authentication** — register, session-based login/logout, role-based access control (does not count toward the functionalities above, per the assignment)
- **Server-side validation** on every form, with field-level error messages and custom business exceptions handled by a global `@ControllerAdvice`

## Web Pages

Home (static) · Register · Login · Classes list · Class details · Class form (create/edit) · My bookings · Trainers list · Trainer form (create/edit) · Error page.

## Roles & Access

- **Guest** — register, login, and browse classes
- **Member** — book, reschedule, and cancel classes; view own bookings
- **Trainer** — manage fitness classes and trainer profiles

## Demo Accounts

Seeded automatically on first startup:

| Username | Password | Role |
|----------|----------|------|
| `trainer` | `trainer123` | TRAINER |
| `member` | `member123` | MEMBER |

## Running Locally

Prerequisites: Java 21 (a newer JDK such as 25 also works as the build/runtime — the project targets Java 21 bytecode), Docker.

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
