# TDOP Backend

The **Tanzania Digital Opportunity Platform (TDOP)** backend REST API. Built with Spring Boot, it connects job seekers, organizations, and platform administrators — managing opportunities, applications, organization verification, reports, and notifications.

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Security | Spring Security + JWT (jjwt 0.12) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation |
| Email | Spring Mail |
| Build | Maven |
| Codegen | Lombok |

## Getting Started

### Prerequisites

- Java 21 (JDK 21+)
- Maven 3.8+
- PostgreSQL running locally (port 5432)

### 1. Create the database

```sql
CREATE DATABASE tdop;
```

Schema and seed data are applied automatically by Flyway on startup (`db/migration`).

### 2. Configure environment

Copy the example and set your PostgreSQL password:

```bash
cp .env.example .env
# or export the variables in your shell, e.g.
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

All configuration values referenced via `${ENV_VAR:default}` in `application.yml` — no secrets are committed to the repository.

### 3. Run

```bash
mvn spring-boot:run
```

The server listens on `http://localhost:8081` (API base path `/api/v1`).

| Profile | File | Notes |
|---|---|---|
| Default | `application.yml` | Runs on port 8081 |
| dev | `application-dev.yml` | Uses the `tdop_dev` database |
| prod | `application-prod.yml` | Reads credentials from environment |

### Health check

```bash
curl http://localhost:8081/api/v1/profile
# -> Profile view
```

## Demo Accounts

Seeded by `V2__seed_data.sql`. Passwords are BCrypt-hashed (`seeker123`, `org123`, `admin123`):

| Role | Email | Password |
|---|---|---|
| Seeker | `john.mwangi@email.com` | `seeker123` |
| Organization | `info@tanzgold.com` | `org123` |
| Admin | `admin@tdop.go.tz` | `admin123` |

## Authorization Model

Three roles — `SEEKER`, `ORGANIZATION`, `ADMIN`:

| URL prefix | Access |
|---|---|
| `/api/v1/auth/**` | Public (login, register, refresh) |
| `/api/v1/public/**` | Public |
| `/api/v1/admin/**` | `ADMIN` |
| `/api/v1/organization/**` | `ORGANIZATION` or `ADMIN` |
| `/api/v1/verify/**` | `ORGANIZATION` or `ADMIN` |
| everything else | Authenticated users |

JWT access tokens (24 h) and refresh tokens (7 days) returned by `/auth/login`. Send `Authorization: Bearer <token>` on subsequent requests.

## Main API Endpoints

### Auth
| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/auth/login` | Login, returns `{token, refreshToken, email, fullName, role}` |
| POST | `/api/v1/auth/register` | Register a new user |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| POST | `/api/v1/auth/logout` | Logout |
| GET | `/api/v1/auth/profile` | Current profile stub |

### Opportunities
| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/opportunities` | Paginated opportunity listing |
| GET | `/api/v1/opportunities/search` | Search opportunities |
| GET | `/api/v1/opportunities/filter/{category}` | Filter by category |
| POST / PUT / DELETE | `/api/v1/opportunities` | Create / update / delete (admin) |
| POST | `/api/v1/organization/opportunities` | Create opportunity (organization) |
| GET / PUT | `/api/v1/organization/opportunities/{id}` | Manage own opportunity |
| PUT | `/api/v1/admin/opportunities/{id}/verify` | Verify an opportunity |
| DELETE | `/api/v1/admin/opportunities/{id}/moderate` | Moderate an opportunity |
| GET | `/api/v1/admin/opportunities/analytics` | Opportunity analytics |

### Applications, Saved, Compare
| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/applications` | Apply to an opportunity |
| GET | `/api/v1/applications/me` | My applications |
| PUT | `/api/v1/applications/{id}/status` | Update application status |
| GET / POST / DELETE | `/api/v1/saved` | Saved opportunities |
| POST | `/api/v1/compare` | Compare opportunities |

### Verification & Reports
| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/verify` | Submit organization verification |
| GET | `/api/v1/verify/{id}` | Get verification request |
| POST | `/api/v1/reports` | Submit a report |
| GET | `/api/v1/reports` | List reports |

### Admin
| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/admin/analytics/dashboard` | Platform metrics |
| GET | `/api/v1/admin/users` | List users |
| PUT | `/api/v1/admin/users/{id}/suspend` | Suspend a user |
| PUT | `/api/v1/admin/users/{id}/role` | Change user role |
| GET | `/api/v1/admin/reports` | Review reports |
| GET | `/api/v1/admin/auth/users` | Admin user directory |

### Organization profile
| Method | Path | Description |
|---|---|---|
| GET / PUT | `/api/v1/organization/profile` | Organization public profile |

## Project Layout

```
src/main/java/tdop/
├── config/         # Security config, JWT filter, CORS, Web config
├── controller/     # REST controllers (incl. admin/ and organization/ sub-packages)
├── entity/         # JPA entities + enums
├── exception/      # Global exception handler
├── mapper/         # Entity <-> DTO mapping
├── matching/       # Opportunity matching service
├── notification/   # Email + in-app notifications
├── repository/     # Spring Data repositories
├── service/        # Business logic
└── verification/   # Organization verification
```

## Testing

```bash
mvn test
```

## Env Variables

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `5432` / `tdop` | PostgreSQL connection |
| `DB_USERNAME` | `postgres` | DB user |
| `DB_PASSWORD` | *(none — required)* | DB password |
| `JWT_SECRET` | dev placeholder | Base64 signing key (>= 256 bits) |
| `MAIL_*` | empty | SMTP for email notifications |
| `APP_URL` | `http://localhost:8081/api/v1` | Public API base URL |

## Related

- [TDOP Frontend](../TDOP-frontend/README.md) — React + TypeScript SPA
- [TDOP Root README](../README.md) — Full project overview