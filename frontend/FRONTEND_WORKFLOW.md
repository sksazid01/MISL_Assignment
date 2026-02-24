# Frontend Workflow

## Entry Point

**File:** `src/main.jsx`

```jsx
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
```

Vite (via `index.html`) loads `src/main.jsx` as the application root. React mounts the component tree inside the `#root` div.

---

## Technology Stack

| Layer         | Technology                          |
|---------------|-------------------------------------|
| Framework     | React 18                            |
| Routing       | React Router v6                     |
| HTTP Client   | Axios (with interceptors)           |
| Build tool    | Vite                                |
| Styling       | Plain CSS (per-feature stylesheets) |
| Deployment    | Vercel / Nginx (Docker)             |

---

## Application Bootstrap (`App.jsx`)

```
main.jsx
  └── App
        └── <Router>          — React Router context
              └── <AuthProvider>   — Global auth state
                    └── <Routes>
                          ├── Public Routes
                          └── Protected Routes (wrapped in <ProtectedRoute>)
```

`App.jsx` is responsible for:
1. Providing the routing context (`BrowserRouter`).
2. Wrapping all routes inside `AuthProvider` so every component can access auth state.
3. Declaring route-level access control via `ProtectedRoute`.

---

## Route Map

```
/                        → Landing          (public)
/login                   → Login            (public)
/register                → Register         (public)

/dashboard               → Layout (shell)   (protected)
  /dashboard  (index)    → Dashboard
  /dashboard/employees   → EmployeeList
  /dashboard/employees/new        → EmployeeForm   (ADMIN only)
  /dashboard/employees/edit/:id   → EmployeeForm   (ADMIN only)
  /dashboard/employees/:id        → EmployeeDetails
  /dashboard/leaves               → LeaveList
  /dashboard/leaves/new           → LeaveForm
  /dashboard/leaves/edit/:id      → LeaveForm
  /dashboard/leaves/:id           → LeaveDetails
  /dashboard/leaves/pending       → PendingLeaves  (ADMIN only)

*                        → redirect to /   (catch-all)
```

---

## Auth State Management (`AuthContext.jsx`)

`AuthProvider` maintains global authentication state and exposes it via React Context:

```
App mounts
    │
    ▼
AuthProvider useEffect → checkAuth()
    │  calls authService.getCurrentUser()   → GET /api/auth/me
    │  calls employeeService.getMyEmployee() → GET /api/employees/me
    │
    ├── success → setUser(userData), setEmployee(employeeData)
    └── failure → setUser(null), setEmployee(null)
```

### Context API

| Value / Function | Description                                               |
|------------------|-----------------------------------------------------------|
| `user`           | Authenticated user object (id, username, email, role)     |
| `employee`       | Employee profile linked to the user (or `null`)           |
| `loading`        | `true` while the initial auth check is in progress        |
| `login(creds)`   | Calls `authService.login()` then refreshes context        |
| `register(data)` | Calls `authService.register()`                            |
| `logout()`       | Calls `authService.logout()`, clears user/employee state  |
| `isAdmin()`      | Returns `true` when `user.role === 'ADMIN'`               |
| `checkAuth()`    | Re-fetches current user and employee data from the API    |

---

## Route Protection (`ProtectedRoute.jsx`)

```
<ProtectedRoute adminOnly={false|true}>
    │
    ├── loading === true  → show loading spinner
    ├── user === null     → <Navigate to="/" />
    ├── adminOnly && !isAdmin → <Navigate to="/dashboard" />
    └── else              → render children
```

All pages under `/dashboard` are wrapped in at least one `<ProtectedRoute>`. Admin-only routes (create/edit employee, pending leaves) receive the `adminOnly` prop.

---

## HTTP Layer (`services/api.js`)

All API calls go through a single configured Axios instance:

```
API_BASE_URL = VITE_API_URL || 'http://localhost:8080/api'

axios instance
  withCredentials: true        ← sends httpOnly cookies automatically
  Content-Type: application/json

Request Interceptor
  └── reads accessToken from localStorage
  └── appends Authorization: Bearer <token>   (cross-domain fallback)

Response Interceptor
  ├── 2xx → pass through
  └── 401 → attempt token refresh
            POST /api/auth/refresh (withCredentials: true)
            ├── success → retry original request
            └── failure → window.location.href = '/login'
```

The dual-token strategy (cookie + localStorage) handles both same-origin and cross-domain deployments (e.g., Vercel → Render).

---

## Service Modules

| File                     | Responsibility                                 |
|--------------------------|------------------------------------------------|
| `services/authService.js`     | login, register, logout, getCurrentUser   |
| `services/employeeService.js` | CRUD for employees, getMyEmployee         |
| `services/leaveService.js`    | Apply, update, delete, approve/reject leaves |
| `services/dashboardService.js`| Fetch aggregate dashboard statistics      |

All services use the shared `api` Axios instance, so token refresh and credential forwarding are handled transparently.

---

## Page Components

| Page               | Route                          | Role Access  | Key Behaviour                                        |
|--------------------|--------------------------------|--------------|------------------------------------------------------|
| `Landing`          | `/`                            | Public       | Entry page; links to Login / Register                |
| `Login`            | `/login`                       | Public       | Posts credentials → stores tokens → redirects to `/dashboard` |
| `Register`         | `/register`                    | Public       | Creates a new USER account                           |
| `Dashboard`        | `/dashboard`                   | All auth     | Shows aggregate stats (total employees, leave counts)|
| `EmployeeList`     | `/dashboard/employees`         | All auth     | Table of all employees                               |
| `EmployeeForm`     | `/dashboard/employees/new|edit`| ADMIN only   | Create or edit an employee record                    |
| `EmployeeDetails`  | `/dashboard/employees/:id`     | All auth     | Read-only employee profile                           |
| `LeaveList`        | `/dashboard/leaves`            | All auth     | All leaves; user sees own; admin sees all            |
| `LeaveForm`        | `/dashboard/leaves/new|edit`   | All auth     | Apply for or edit a PENDING leave                    |
| `LeaveDetails`     | `/dashboard/leaves/:id`        | All auth     | Leave details with approval info                     |
| `PendingLeaves`    | `/dashboard/leaves/pending`    | ADMIN only   | Review and approve/reject pending requests           |

---

## Shared Components

| Component        | Purpose                                                   |
|------------------|-----------------------------------------------------------|
| `Layout`         | Persistent shell (Navbar + `<Outlet />`) for all dashboard pages |
| `Navbar`         | Top navigation bar; shows role, username, logout button   |
| `ProtectedRoute` | Guards routes by authentication status and role           |

---

## Request Flow (End-to-End Example)

```
User clicks "Apply Leave"
    │
    ▼
LeaveForm submits → leaveService.createLeave(data)
    │
    ▼
api.js (Axios) → POST /api/leaves
  headers: Authorization: Bearer <token>
  cookies: accessToken (httpOnly)
    │
    ▼
Backend JwtAuthenticationFilter validates token
    │
    ▼
LeaveController.applyLeave()
  → saves Leave with status=PENDING
  → responds 201 Created + LeaveResponse JSON
    │
    ▼
LeaveForm receives response → navigates to /dashboard/leaves
```

---

## Environment Variables

| Variable       | Purpose                                  | Default                        |
|----------------|------------------------------------------|--------------------------------|
| `VITE_API_URL` | Backend base URL used by Axios           | `http://localhost:8080/api`    |

Set in `.env` (local) or via Vercel environment settings (production).

---

## Docker / Production

The frontend `Dockerfile` builds a static Vite bundle and serves it via **Nginx** on port **80**. `nginx.conf` proxies `/api/**` requests to the backend service, enabling the frontend to work without CORS issues in the Docker Compose environment.
