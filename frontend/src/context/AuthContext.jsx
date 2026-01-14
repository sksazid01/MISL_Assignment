import { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';
import employeeService from '../services/employeeService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [employee, setEmployee] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on mount
    checkAuth();
  }, []); 

  const checkAuth = async () => {
    try {
      const userData = await authService.getCurrentUser();
      setUser(userData);
      
      // Fetch employee data if user is logged in
      try {
        const employeeData = await employeeService.getMyEmployee();
        // Check if response has id (valid employee) or is a message (no employee)
        if (employeeData.id) {
          setEmployee(employeeData);
        } else {
          setEmployee(null);
        }
      } catch (err) {
        setEmployee(null);
      }
    } catch (error) {
      setUser(null);
      setEmployee(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    const response = await authService.login(credentials);
    // Login response already contains user data, set it directly
    setUser(response);
    
    // Fetch employee data if user is logged in
    try {
      const employeeData = await employeeService.getMyEmployee();
      if (employeeData.id) {
        setEmployee(employeeData);
      } else {
        setEmployee(null);
      }
    } catch (err) {
      setEmployee(null);
    }
    
    return response;
  };

  const register = async (userData) => {
    const response = await authService.register(userData);
    return response;
  };

  const logout = async () => { 
    await authService.logout();
    setUser(null);
    setEmployee(null);
  };

  const isAdmin = () => {
    return user?.role === 'ADMIN';
  };

  // pass those function value to children
  const value = {
    user,
    employee,
    loading,
    login,
    register,
    logout,
    isAdmin,
    checkAuth,
  };

  return <AuthContext.Provider value={value}>
    {children}
    </AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
