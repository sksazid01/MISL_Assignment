# Employee Leave Tracker

A full-stack web application for managing employee information and leave requests with JWT-based authentication and role-based access control.

## Tech Stack

- **Backend**: Spring Boot 4.0.1, Java 21, Spring Security, PostgreSQL
- **Frontend**: React 19, Vite, Axios, React Router
- **Authentication**: JWT (Access + Refresh Tokens)
- **Deployment**: Docker, Nginx

## Features

- **User Registration**: Anyone can register as a user account
- **User Access**: Users can view the dashboard and employee list (read-only)
- **Admin Management**: Admins can promote users to employees and manage employee information
- **Employee Leave System**: 
  - Employees can submit leave applications
  - Employees can edit their own pending leave requests
- **Admin Controls**: 
  - Full access to edit employee details
  - Review and manage (approve/reject) pending leave applications
- **Role-Based Security**: Automatic authorization based on user roles (Admin, User, Employee)

## Setup & Run Instructions

### Using Docker (Recommended)
```bash
docker-compose up --build
```
- Frontend: http://localhost:3000/login
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
Access at: http://localhost:5173 (dev server)

### Default Credentials
- **Admin**: Username: **admin** | Password: **admin123**
- **User**: Username: **skk** | Password: **skk@gmail.com**
- **Employee user**: Username: **mehedi** | Password: **mehedi@gmail.com**

## Architecture

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

## How It Works

### Authentication Flow
1. **User Login**: Credentials sent to backend authentication endpoint
2. **Token Generation**: Backend validates credentials and issues JWT access token (15 min) and refresh token (7 days)
3. **Token Storage**: Tokens stored in HTTP-only cookies for security
4. **Authenticated Requests**: Access token sent with each API request via Authorization header
5. **Token Refresh**: When access token expires, refresh token automatically requests new access token
6. **Logout**: Tokens cleared from cookies, invalidating session

### Request Lifecycle
1. **Frontend**: React component triggers action (e.g., fetch employees)
2. **API Service**: Axios interceptor adds JWT token to request header
3. **Backend**: Spring Security validates token and extracts user details
4. **Authorization**: Role-based checks ensure user has required permissions
5. **Business Logic**: Service layer processes request and interacts with database
6. **Response**: Data returned as JSON, frontend updates UI

### Role-Based Access Control
- **Database Level**: JPA entities link users to employees via userId
- **Service Layer**: Methods filter data based on user role and ownership
- **API Level**: Spring Security annotations (`@PreAuthorize`) restrict endpoint access
- **Frontend**: UI components conditionally render based on user role

### User Roles
- **ADMIN**: Full access to all employees and leave requests
- **USER**: Access to own employee profile and leave requests

## API Testing

For comprehensive API testing, import the Postman collection: [JWT_Authentication_API.postman_collection.json](backend/JWT_Authentication_API.postman_collection.json)

The collection includes pre-configured requests for authentication, employee management, and leave management with automatic token handling.

## Additional Documentation

- **Backend Details**: [backend/README.md](backend/README.md)
- **Frontend Details**: [frontend/README.md](frontend/README.md)
