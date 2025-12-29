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
  const [recentLeaves, setRecentLeaves] = useState([]);
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

      setRecentLeaves(data.recentLeaves || []);
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

      <div className="quick-actions">
        <h2>Quick Actions</h2>
        <div className="action-buttons-grid">
          {(isAdmin() || employee) && (
            <Link to="/leaves/new" className="action-card">
              <span className="action-icon">📝</span>
              <h3>Apply for Leave</h3>
              <p>Submit a new leave application</p>
            </Link>
          )}
          {isAdmin() && (
            <>
              <Link to="/employees/new" className="action-card">
                <span className="action-icon">➕</span>
                <h3>Add Employee</h3>
                <p>Register a new employee</p>
              </Link>
              <Link to="/leaves/pending" className="action-card">
                <span className="action-icon">✓</span>
                <h3>Review Leaves</h3>
                <p>Approve or reject pending leaves</p>
              </Link>
            </>
          )}
          <Link to="/employees" className="action-card">
            <span className="action-icon">👥</span>
            <h3>View Employees</h3>
            <p>Browse all employees</p>
          </Link>
        </div>
      </div>

      <div className="recent-section">
        <div className="section-header">
          <h2>Recent Leave Applications</h2>
          <Link to="/leaves" className="view-all-link">
            View All →
          </Link>
        </div>
        
        {recentLeaves.length > 0 ? (
          <div className="recent-leaves-table">
            <table>
              <thead>
                <tr>
                  <th>Employee</th>
                  <th>Type</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                  <th>Days</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {recentLeaves.map((leave) => (
                  <tr key={leave.id}>
                    <td>{leave.employeeName}</td>
                    <td>{leave.leaveType}</td>
                    <td>{formatDate(leave.startDate)}</td>
                    <td>{formatDate(leave.endDate)}</td>
                    <td>{leave.totalDays}</td>
                    <td>
                      <span className={`status-badge ${getStatusColor(leave.status)}`}>
                        {leave.status}
                      </span>
                    </td>
                    <td>
                      <Link to={`/leaves/${leave.id}`} className="btn btn-sm">
                        View
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="empty-state">
            <p>📋 No leave applications yet</p>
            <Link to="/leaves/new" className="btn btn-primary">
              Apply for Your First Leave
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
