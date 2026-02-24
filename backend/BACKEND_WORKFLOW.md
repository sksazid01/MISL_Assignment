# Backend Workflow

## Entry Point

**File:** `src/main/java/com/Assignment/AssignmentApplication.java`

```java
@SpringBootApplication
@EnableScheduling
public class AssignmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssignmentApplication.class, args);
    }
}
```

Spring Boot bootstraps the entire application from here. `@EnableScheduling` activates any `@Scheduled` tasks registered in the context.

---

## Startup Sequence

1. Spring Boot auto-configures beans, the data source, JPA, and the security filter chain.
2. **`DataInitializer`** (`config/DataInitializer.java`) runs as a `CommandLineRunner`:
   - Checks whether the `admin` user already exists in the database.
   - If not, seeds a default admin account (`username: admin`, `password: admin123`, `role: ADMIN`).

---

## Technology Stack

| Layer        | Technology                              |
|--------------|-----------------------------------------|
| Framework    | Spring Boot 3.x                         |
| Security     | Spring Security + JWT (stateless)       |
| Database     | PostgreSQL (via Spring Data JPA / Hibernate) |
| Build tool   | Maven (`mvnw`)                          |
| Lombok       | Reduces boilerplate (builders, getters) |
| Bean validation | Jakarta Validation (`@Valid`)        |

---

## Security & Request Lifecycle

Every HTTP request passes through this pipeline:

```
Incoming Request
      │
      ▼
┌─────────────────────────────┐
│  JwtAuthenticationFilter    │  (runs once per request)
│  1. Read accessToken cookie │
│  2. Fall back to Bearer hdr │
│  3. jwtUtil.validateToken() │
│  4. Load UserDetails        │
│  5. Set SecurityContext     │
└─────────────────────────────┘
      │
      ▼
┌─────────────────────────────┐
│  SecurityConfig Rules       │
│  • /api/auth/**  → public   │
│  • /api/health   → public   │
│  • /api/employees/** → AUTH │
│  • /api/leaves/**   → AUTH  │
│  • everything else  → AUTH  │
└─────────────────────────────┘
      │
      ▼
   Controller
```

### JWT Token Strategy

| Token         | Lifetime  | Storage                       |
|---------------|-----------|-------------------------------|
| Access Token  | 24 hours  | httpOnly cookie + response body (localStorage fallback) |
| Refresh Token | 7 days    | httpOnly cookie               |

Cookies are set with `HttpOnly=true`, `Secure=true`, `SameSite=None` to support cross-domain requests (e.g., Vercel frontend → Render backend).

Token extraction order in `JwtAuthenticationFilter`:
1. `accessToken` cookie
2. `Authorization: Bearer <token>` header (backward-compatibility fallback)

---

## Package Structure

```
com.Assignment/
├── AssignmentApplication.java   ← Entry point
├── config/
│   ├── SecurityConfig.java      ← Filter chain, CORS, role rules
│   ├── DataInitializer.java     ← Seeds default admin on startup
│   └── DotEnvConfig.java        ← Environment variable loading
├── security/
│   ├── JwtAuthenticationFilter.java  ← Per-request JWT validation
│   └── JwtUtil.java                  ← Token generation & validation
├── controller/
│   ├── AuthController.java      ← /api/auth/**
│   ├── EmployeeController.java  ← /api/employees/**
│   ├── LeaveController.java     ← /api/leaves/**
│   ├── DashboardController.java ← /api/dashboard/**
│   ├── HealthController.java    ← /api/health
│   └── TestController.java
├── service/
│   └── CustomUserDetailsService.java  ← Loads UserDetails from DB
├── entity/
│   ├── User.java        ← Auth user (role: ADMIN | USER)
│   ├── Employee.java    ← Employee profile linked to User
│   ├── Leave.java       ← Leave request
│   ├── Role.java        ← Enum: ADMIN, USER
│   ├── LeaveStatus.java ← Enum: PENDING, APPROVED, REJECTED
│   └── LeaveType.java   ← Enum of leave categories
├── dto/                 ← Request/Response POJOs
├── repository/          ← Spring Data JPA interfaces
└── exception/           ← Custom exception handlers
```

---

## REST API Endpoints

### Authentication — `/api/auth/**` (public)

| Method | Path               | Access  | Description                          |
|--------|--------------------|---------|--------------------------------------|
| POST   | `/auth/register`   | Public  | Register a new user                  |
| POST   | `/auth/login`      | Public  | Authenticate; sets JWT cookies       |
| POST   | `/auth/refresh`    | Public  | Rotate tokens using refresh cookie   |
| POST   | `/auth/logout`     | Public  | Clears JWT cookies (maxAge = 0)      |
| GET    | `/auth/me`         | Auth    | Returns currently authenticated user |
| GET    | `/auth/users`      | ADMIN   | List all registered users            |

### Employees — `/api/employees/**`

| Method | Path                              | Access       | Description                        |
|--------|-----------------------------------|--------------|------------------------------------|
| GET    | `/employees`                      | ADMIN, USER  | List all employees                 |
| GET    | `/employees/{id}`                 | ADMIN, USER  | Get employee by ID                 |
| GET    | `/employees/active`               | ADMIN, USER  | List active employees              |
| GET    | `/employees/department/{dept}`    | ADMIN, USER  | Filter by department               |
| POST   | `/employees`                      | ADMIN only   | Create employee (link to User)     |
| PUT    | `/employees/{id}`                 | ADMIN only   | Update employee record             |
| DELETE | `/employees/{id}`                 | ADMIN only   | Delete employee                    |

### Leaves — `/api/leaves/**`

| Method | Path                              | Access       | Description                         |
|--------|-----------------------------------|--------------|-------------------------------------|
| GET    | `/leaves`                         | ADMIN, USER  | List all leaves (filterable)        |
| GET    | `/leaves/{id}`                    | ADMIN, USER  | Get leave by ID                     |
| GET    | `/leaves/employee/{employeeId}`   | ADMIN, USER  | All leaves for an employee          |
| GET    | `/leaves/pending`                 | ADMIN only   | All PENDING leave requests          |
| GET    | `/leaves/stats/employee/{id}`     | ADMIN, USER  | Leave statistics for an employee    |
| POST   | `/leaves`                         | ADMIN, USER  | Apply for leave (status = PENDING)  |
| PUT    | `/leaves/{id}`                    | ADMIN, USER  | Update a PENDING leave              |
| PATCH  | `/leaves/{id}/status`             | ADMIN only   | Approve or reject a leave           |
| DELETE | `/leaves/{id}`                    | ADMIN, USER  | Delete a PENDING leave              |

### Dashboard — `/api/dashboard/**`

| Method | Path                 | Access      | Description                   |
|--------|----------------------|-------------|-------------------------------|
| GET    | `/dashboard/stats`   | ADMIN, USER | Aggregate counts for dashboard|

---

## Data Model

```
User (1) ──────── (1) Employee (1) ──────── (N) Leave
  │                     │
role: ADMIN|USER    department, position,
                    hireDate, isActive, ...
```

- A **User** manages authentication credentials and role.
- An **Employee** is a profile record linked one-to-one to a User.
- A **Leave** is owned by an Employee and tracks type, date range, status, approver, and rejection reason.

---

## Configuration

Key settings in `src/main/resources/application.yml`:

```yaml
spring.datasource.url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/defaultdb}
spring.jpa.hibernate.ddl-auto: update     # auto-updates schema on startup

jwt.expiration: 86400000                  # 24 h
jwt.refresh-expiration: 604800000         # 7 days
```

All sensitive values (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`) are read from environment variables with safe defaults for local development.

---

## Docker

The backend `Dockerfile` builds a production JAR and exposes port **8080**. `docker-compose.yml` at the project root orchestrates the backend together with the frontend and (optionally) a PostgreSQL container.
