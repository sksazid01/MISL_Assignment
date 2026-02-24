# Backend - Employee Leave Tracker API

Spring Boot REST API with JWT authentication for managing employees and leave requests.

## 🚀 Quick Start

### Run with Maven
```bash
./mvnw spring-boot:run
```

### Build JAR
```bash
./mvnw clean package
java -jar target/Assignment-0.0.1-SNAPSHOT.jar
```

### Run with Docker
```bash
docker build -t leave-tracker-backend .
docker run -p 8080:8080 leave-tracker-backend
```

## 🔧 Configuration

Configuration files in `src/main/resources/`:
- `application.yml` - Local development config
- `application-docker.yml` - Docker environment config

### Key Settings
```yaml
server:
  port: 8080

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000      # 24 hours
  refresh-expiration: 604800000  # 7 days
```

## 📁 Project Structure

```
src/main/java/com/Assignment/
├── config/              # Security & CORS configuration
├── controller/          # REST API endpoints
├── dto/                 # Request/Response objects
├── entity/              # JPA entities (User, Employee, Leave)
├── exception/           # Custom exception handling
├── repository/          # Data access layer
├── security/            # JWT utilities & filters
└── service/             # Business logic
```

## 🔑 Security

- **Authentication**: JWT tokens (access + refresh)
- **Password Encryption**: BCrypt
- **Authorization**: Role-based (USER, ADMIN)
- **CORS**: Configurable origins

## 📡 API Endpoints

### Authentication (`/api/auth`)
- `POST /register` - User registration
- `POST /login` - User login
- `POST /refresh` - Refresh access token
- `POST /logout` - Logout

### Employees (`/api/employees`)
- `GET /` - List all employees (ADMIN) or own profile (USER)
- `GET /{id}` - Get employee by ID
- `POST /` - Create employee
- `PUT /{id}` - Update employee
- `DELETE /{id}` - Delete employee (ADMIN only)

### Leaves (`/api/leaves`)
- `GET /` - List leaves (filtered by role)
- `GET /{id}` - Get leave details
- `POST /` - Submit leave request
- `PUT /{id}` - Update leave request
- `PATCH /{id}/status` - Approve/reject (ADMIN only)

## 🛠️ Technologies

- **Java**: 21
- **Spring Boot**: 4.0.1
- **Spring Security**: 7.x
- **Spring Data JPA**: PostgreSQL database
- **JWT**: jjwt 0.12.5
- **Build Tool**: Maven

## 📚 Additional Documentation

Detailed guides in `extra_files/`:
- `IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- `JWT_AUTH_README.md` - JWT authentication guide
- `AUTHENTICATION_FLOW.md` - Auth flow diagrams
- `LEAVE_TRACKER_API.md` - API documentation
- `QUICK_START.md` - Quick start guide
