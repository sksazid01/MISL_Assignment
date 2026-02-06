import { Link } from 'react-router-dom';
import '../styles/Landing.css';

const Landing = () => {
  return (
    <div className="landing-container">
      <div className="landing-content">
        <div className="landing-hero">
          <h1 className="landing-title">Employee Leave Management System</h1>
          <p className="landing-subtitle">
            Streamline your leave management process with our comprehensive solution
          </p>
        </div>

        <div className="landing-features">
          <div className="feature-card">
            <div className="feature-icon">📅</div>
            <h3>Leave Tracking</h3>
            <p>Track and manage employee leave requests efficiently</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">👥</div>
            <h3>Employee Management</h3>
            <p>Manage employee information and leave balances</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">📊</div>
            <h3>Dashboard Analytics</h3>
            <p>Get insights with comprehensive reporting and analytics</p>
          </div>
        </div>

        <div className="landing-actions">
          <Link to="/login" className="btn btn-primary">
            Login
          </Link>
          <Link to="/register" className="btn btn-secondary">
            Register
          </Link>
        </div>

        <div className="landing-footer">
          <p>© {new Date().getFullYear()} Employee Leave Management System. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default Landing;
