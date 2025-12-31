# Employee Leave Tracker

A full-stack web application for managing employee information and leave requests with JWT-based authentication and role-based access control.

## 🚀 Quick Setup

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

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 4.0.1, Java 21, Spring Security, PostgreSQL → [Backend README](backend/README.md)
- **Frontend**: React 19, Vite, Axios, React Router → [Frontend README](frontend/README.md)
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

## � How It Works

### Authentication Flow
1. **User Login**: Credentials sent to `/api/auth/login`
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

## �📋 Features

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

## 🧪 API Testing with Postman

Import the Postman collection for easy API testing:
- **Collection File**: `backend/JWT_Authentication_API.postman_collection.json`
- **Import**: Open Postman → Import → Select the JSON file
- **Features**: Pre-configured requests with descriptions, authentication endpoints, employee/leave CRUD operations
- **Authentication**: Tokens are managed via httpOnly cookies automatically (no manual header setup needed)

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
- PostgreSQL Driver
- jjwt (JWT implementation)
- Lombok

### Frontend
- React & React Router DOM
- Axios
- Vite
- date-fns

## 📝 Additional Documentation

### Module-Specific Documentation
- **Backend**: [backend/README.md](backend/README.md) - API setup and configuration
- **Frontend**: [frontend/README.md](frontend/README.md) - UI components and development

### Detailed Guides
- Backend: `backend/extra_files/` - Implementation guides and API docs
- Frontend: `frontend/extra_files/` - Component documentation and testing guides
