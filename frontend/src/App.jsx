import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import PublicRoute from './components/PublicRoute';
import Layout from './components/Layout';

// Auth Pages
import Landing from './pages/Landing';
import Login from './pages/Login';
import Register from './pages/Register';

// Dashboard
import Dashboard from './pages/Dashboard';

// Employee Pages
import EmployeeList from './pages/EmployeeList';
import EmployeeForm from './pages/EmployeeForm';
import EmployeeDetails from './pages/EmployeeDetails';

// Leave Pages
import LeaveList from './pages/LeaveList';
import LeaveForm from './pages/LeaveForm';
import LeaveDetails from './pages/LeaveDetails';
import PendingLeaves from './pages/PendingLeaves';

import './App.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Public Routes – redirect to /dashboard if already logged in */}
          <Route path="/" element={<PublicRoute><Landing /></PublicRoute>} />
          <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
          <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />

          {/* Protected Routes */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            {/* default child, that Loads dashboard inside layout */}
            <Route index element={<Dashboard />} />   
            
            {/* Employee Routes */}
            <Route path="employees" element={<EmployeeList />} />
            <Route path="employees/new" element={
              <ProtectedRoute adminOnly>
                <EmployeeForm />
              </ProtectedRoute>
            } />
            <Route path="employees/edit/:id" element={
              <ProtectedRoute adminOnly>
                <EmployeeForm />
              </ProtectedRoute>
            } />
            <Route path="employees/:id" element={<EmployeeDetails />} />

            {/* Leave Routes */}
            <Route path="leaves" element={<LeaveList />} />
            <Route path="leaves/new" element={<LeaveForm />} />
            <Route path="leaves/edit/:id" element={<LeaveForm />} />
            <Route path="leaves/:id" element={<LeaveDetails />} />
            <Route path="leaves/pending" element={
              <ProtectedRoute adminOnly>
                <PendingLeaves />
              </ProtectedRoute>
            } />
          </Route>

          {/* Catch all - Redirect to root */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
