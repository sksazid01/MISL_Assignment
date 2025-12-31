# Employee Leave Tracker

A full-stack web application for managing employee information and leave requests with JWT-based authentication and role-based access control.

## 🚀 Quick Setup

### Using Docker (Recommended)
```bash
docker-compose up --build
```
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

### Manual Setup

#### Backend
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.4.1, Java 17, Spring Security
- **Frontend**: React 18, Vite, Axios, React Router
- **Authentication**: JWT (Access + Refresh Tokens)
- **Deployment**: Docker, Nginx

### Project Structure
```
├── backend/               # Spring Boot REST API
│   ├── src/main/java/
│   │   └── com/Assignment/
│   │       ├── config/        # Security & CORS configuration
│   │       ├── controller/    # REST endpoints
│   │       ├── dto/           # Data transfer objects
│   │       ├── entity/        # JPA entities
│   │       ├── repository/    # Data access layer
│   │       ├── security/      # JWT utilities
│   │       └── service/       # Business logic
│   └── src/main/resources/
│       └── application.yml    # Configuration
│
├── frontend/              # React SPA
│   ├── src/
│   │   ├── components/    # Reusable components
│   │   ├── context/       # Authentication state
│   │   ├── pages/         # Route components
│   │   ├── services/      # API integration
│   │   └── styles/        # CSS modules
│   └── vite.config.js
│
└── docker-compose.yml     # Multi-container orchestration
```

## 📋 Features

### Core Functionality
- **Employee Management**: CRUD operations for employee records
- **Leave Management**: Submit, approve/reject leave requests
- **Dashboard**: Overview statistics and pending actions
- **Role-Based Access**: USER and ADMIN roles with different permissions

### Security Features
- JWT-based stateless authentication
- Automatic token refresh mechanism
- BCrypt password encryption
- Protected routes and API endpoints
- CORS configuration for cross-origin requests

### User Roles
- **ADMIN**: Full access to all employees and leave requests
- **USER**: Access to own employee profile and leave requests

## 🔑 Default Credentials

Admin user is automatically created on first run:
- **Username**: admin
- **Password**: admin123

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - User logout

### Employees
- `GET /api/employees` - List all employees
- `GET /api/employees/{id}` - Get employee details
- `POST /api/employees` - Create employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee

### Leaves
- `GET /api/leaves` - List leaves (filtered by user role)
- `GET /api/leaves/{id}` - Get leave details
- `POST /api/leaves` - Submit leave request
- `PUT /api/leaves/{id}` - Update leave request
- `PATCH /api/leaves/{id}/status` - Approve/reject leave

## 🛠️ Configuration

### Backend Configuration
Key settings in `backend/src/main/resources/application.yml`:
- Server port: 8080
- JWT secret and expiration times
- CORS allowed origins

### Frontend Configuration
API base URL in `frontend/src/services/api.js`:
```javascript
baseURL: 'http://localhost:8080/api'
```

## 📦 Dependencies

### Backend
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- jjwt (JWT implementation)
- Lombok

### Frontend
- React & React Router DOM
- Axios
- Vite

## 📝 Additional Documentation

- Backend: `backend/extra_files/` - Detailed implementation guides
- Frontend: `frontend/extra_files/` - Component documentation and testing guides
