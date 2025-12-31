import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import dashboardService from '../services/dashboardService';
import { useAuth } from '../context/AuthContext';
import { formatDate } from '../utils/dateUtils';
import '../styles/Dashboard.css';

const Dashboard = () => {
  const { user, employee, isAdmin } = useAuth();
  const [stats, setStats] = useState({
    totalEmployees: 0,
    activeEmployees: 0,
    totalLeaves: 0,
    pendingLeaves: 0,
    approvedLeaves: 0,
    rejectedLeaves: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const data = await dashboardService.getDashboardStats();
      
      setStats({
        totalEmployees: data.totalEmployees,
        activeEmployees: data.activeEmployees,
        totalLeaves: data.totalLeaves,
        pendingLeaves: data.pendingLeaves,
        approvedLeaves: data.approvedLeaves,
        rejectedLeaves: data.rejectedLeaves,
      });
    } catch (err) {
      console.error('Failed to fetch dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'APPROVED':
        return 'success';
      case 'REJECTED':
        return 'danger';
      case 'PENDING':
        return 'warning';
      default:
        return '';
    }
  };

  if (loading) return <div className="loading">Loading dashboard...</div>;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Welcome, {user?.username}!</h1>
        <p>Here's an overview of the Employee Leave Management System</p>
        {!isAdmin() && !employee && (
          <div style={{ marginTop: '16px', padding: '12px', backgroundColor: '#fff3cd', color: '#856404', borderRadius: '8px', border: '1px solid #ffeaa7' }}>
            ⚠️ You don't have an employee record yet. Contact your administrator to create one so you can apply for leave.
          </div>
        )}
        {!isAdmin() && employee && (
          <div style={{ marginTop: '16px', padding: '12px', backgroundColor: '#d4edda', color: '#155724', borderRadius: '8px', border: '1px solid #c3e6cb' }}>
            ✓ Employee Status: <strong>{employee.username}</strong> - {employee.department}
          </div>
        )}
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon employee">👥</div>
          <div className="stat-content">
            <h3>Total Employees</h3>
            <p className="stat-value">{stats.totalEmployees}</p>
            <Link to="/employees" className="stat-link">
              View all →
            </Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon active">✓</div>
          <div className="stat-content">
            <h3>Active Employees</h3>
            <p className="stat-value">{stats.activeEmployees}</p>
            <Link to="/employees" className="stat-link">
              View all →
            </Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon leave">📋</div>
          <div className="stat-content">
            <h3>Total Leaves</h3>
            <p className="stat-value">{stats.totalLeaves}</p>
            <Link to="/leaves" className="stat-link">
              View all →
            </Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon pending">⏳</div>
          <div className="stat-content">
            <h3>Pending Leaves</h3>
            <p className="stat-value">{stats.pendingLeaves}</p>
            {isAdmin() && (
              <Link to="/leaves/pending" className="stat-link">
                Review →
              </Link>
            )}
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon approved">✅</div>
          <div className="stat-content">
            <h3>Approved Leaves</h3>
            <p className="stat-value">{stats.approvedLeaves}</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon rejected">❌</div>
          <div className="stat-content">
            <h3>Rejected Leaves</h3>
            <p className="stat-value">{stats.rejectedLeaves}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
