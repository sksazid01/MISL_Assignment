# Frontend - Employee Leave Tracker UI

React-based single-page application for managing employees and leave requests with modern UI/UX.

## 🚀 Quick Start

### Development
```bash
npm install
npm run dev
```
Access at: http://localhost:5173 (dev server)

### Production Build
```bash
npm run build
npm run preview
```

### Docker
```bash
docker build -t leave-tracker-frontend .
docker run -p 3000:80 leave-tracker-frontend
```

## 📁 Project Structure

```
src/
├── components/          # Reusable components
│   ├── Layout.jsx      # Main layout with navbar
│   ├── Navbar.jsx      # Navigation component
│   └── ProtectedRoute.jsx  # Auth route guard
│
├── context/            # Global state management
│   └── AuthContext.jsx # Authentication context
│
├── pages/              # Route pages
│   ├── Login.jsx       # Login page
│   ├── Register.jsx    # Registration page
│   ├── Dashboard.jsx   # Overview dashboard
│   ├── EmployeeList.jsx    # Employee listing
│   ├── EmployeeForm.jsx    # Create/edit employee
│   ├── EmployeeDetails.jsx # Employee details
│   ├── LeaveList.jsx       # Leave requests list
│   ├── LeaveForm.jsx       # Submit leave
│   ├── LeaveDetails.jsx    # Leave details
│   └── PendingLeaves.jsx   # Pending approvals (ADMIN)
│
├── services/           # API integration
│   ├── api.js          # Axios config + interceptors
│   ├── authService.js  # Auth API calls
│   ├── employeeService.js  # Employee API calls
│   ├── leaveService.js     # Leave API calls
│   └── dashboardService.js # Dashboard stats
│
├── styles/             # CSS modules
├── utils/              # Utility functions
└── assets/             # Static assets
```

## 🎨 Features

### Authentication
- Login with username/password
- User registration with role selection
- Automatic token refresh
- Protected routes
- Persistent sessions

### Employee Management
- List employees with filters (All, Active, Department)
- Create/edit employee profiles
- View detailed employee information
- Track employee leave statistics
- Delete employees (ADMIN only)

### Leave Management
- Submit leave requests
- View leave history
- Update pending leaves
- Approve/reject leaves (ADMIN)
- Status tracking (PENDING, APPROVED, REJECTED)

### Dashboard
- Statistics overview
- Quick action buttons
- Recent leave requests
- Role-based views

## 🔧 Configuration

### API Base URL
Update in `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

### Environment Variables
Create `.env` file:
```
VITE_API_URL=http://localhost:8080/api
```

## 🛠️ Technologies

- **React**: 19.2.0
- **React Router**: 7.11.0
- **Axios**: 1.13.2
- **Vite**: 7.2.4
- **date-fns**: 4.1.0
- **Build**: Production-ready with Nginx

## 🎯 Key Components

### Authentication Context
Global state management for user authentication, automatic token handling, and user profile.

### API Interceptor
Automatic token injection and refresh on 401 responses.

### Protected Routes
Route guards that redirect unauthenticated users to login.

### Role-Based UI
Components adapt based on user role (USER vs ADMIN).

## 📚 Additional Documentation

Detailed guides in `extra_files/`:
- `FRONTEND_IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- `QUICK_START_GUIDE.md` - Getting started guide
- `TESTING_GUIDE.md` - Testing instructions
- `UI_IMPROVEMENTS.md` - UI/UX enhancements
- `DOCKER_README.md` - Docker deployment guide
- `ADMIN_CREDENTIALS.md` - Default admin credentials
