import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import employeeService from '../services/employeeService';
import leaveService from '../services/leaveService';
import { formatDate } from '../utils/dateUtils';
import '../styles/Employee.css';

const EmployeeDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [employee, setEmployee] = useState(null);
  const [leaves, setLeaves] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchEmployeeData();
  }, [id]);

  const fetchEmployeeData = async () => {
    setLoading(true);
    try {
      // All API calls run in parallel and Faster than sequential calls
      const [employeeData, leavesData, statsData] = await Promise.all([  
        employeeService.getEmployeeById(id),
        leaveService.getLeavesByEmployee(id),
        leaveService.getEmployeeLeaveStats(id),
      ]);
      setEmployee(employeeData);
      setLeaves(leavesData);
      setStats(statsData);
    } catch (err) {
      setError('Failed to fetch employee details');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!employee) return <div className="error-message">Employee not found</div>;

  return (
    <div className="employee-details-container">
      <div className="page-header">
        <h2>Employee Details</h2>
        <div className="header-actions">
          <Link to={`/employees/edit/${id}`} className="btn btn-primary">
            Edit Employee
          </Link>
          <button onClick={() => navigate('/dashboard/employees')} className="btn btn-secondary">
            Back to List
          </button>
        </div>
      </div>

      <div className="details-grid">
        <div className="details-card">
          <h3>Personal Information</h3>
          <div className="info-row">
            <span className="label">Username:</span>
            <span className="value">{employee.username}</span>
          </div>
          <div className="info-row">
            <span className="label">Email:</span>
            <span className="value">{employee.email}</span>
          </div>
          <div className="info-row">
            <span className="label">Phone:</span>
            <span className="value">{employee.phoneNumber}</span>
          </div>
          <div className="info-row">
            <span className="label">Status:</span>
            <span className={`status-badge ${employee.isActive ? 'active' : 'inactive'}`}>
              {employee.isActive ? 'Active' : 'Inactive'}
            </span>
          </div>
        </div>

        <div className="details-card">
          <h3>Work Information</h3>
          <div className="info-row">
            <span className="label">Department:</span>
            <span className="value">{employee.department}</span>
          </div>
          <div className="info-row">
            <span className="label">Designation:</span>
            <span className="value">{employee.designation}</span>
          </div>
          <div className="info-row">
            <span className="label">Join Date:</span>
            <span className="value">{formatDate(employee.joinDate)}</span>
          </div>
          <div className="info-row">
            <span className="label">Employee ID:</span>
            <span className="value">#{employee.id}</span>
          </div>
        </div>
      </div>

      {stats && (
        <div className="stats-card">
          <h3>Leave Statistics</h3>
          <div className="stats-grid">
            <div className="stat-item">
              <span className="stat-label">Total Leaves</span>
              <span className="stat-value">{stats.totalLeaves || 0}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Approved</span>
              <span className="stat-value success">{stats.approvedLeaves || 0}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Pending</span>
              <span className="stat-value warning">{stats.pendingLeaves || 0}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Rejected</span>
              <span className="stat-value danger">{stats.rejectedLeaves || 0}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Total Days</span>
              <span className="stat-value">{stats.totalDays || 0}</span>
            </div>
          </div>
        </div>
      )}

      <div className="leaves-section">
        <div className="section-header">
          <h3>Leave History</h3>
          <Link to={`/leaves/new?employeeId=${id}`} className="btn btn-primary">
            Apply for Leave
          </Link>
        </div>
        
        {leaves.length > 0 ? (
          <div className="leaves-table">
            
            <table>
              <thead>
                {/* One row */}
                <tr>
                  {/* 6 column or row heading */}
                  <th>Type</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                  <th>Days</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {leaves.map((leave) => (
                  <tr key={leave.id}>
                    <td>{leave.leaveType}</td>
                    <td>{formatDate(leave.startDate)}</td>
                    <td>{formatDate(leave.endDate)}</td>
                    <td>{leave.totalDays}</td>
                    <td>
                      <span className={`status-badge ${leave.status.toLowerCase()}`}>
                        {leave.status}
                      </span>
                    </td>
                    <td>
                      <Link to={`/dashboard/leaves/${leave.id}`} className="btn btn-sm">
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
            <p>No leave records found</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default EmployeeDetails;
