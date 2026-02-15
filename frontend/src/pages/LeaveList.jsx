import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import leaveService from '../services/leaveService';
import { useAuth } from '../context/AuthContext';
import { formatDate } from '../utils/dateUtils';
import '../styles/Leave.css';

const LeaveList = () => {
  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const { employee, isAdmin } = useAuth();

  useEffect(() => {
    fetchLeaves();
  }, [statusFilter]);

  const fetchLeaves = async () => {
    setLoading(true);
    setError('');
    try {
      const filters = {};
      if (statusFilter) filters.status = statusFilter;
      const data = await leaveService.getAllLeaves(filters);
      setLeaves(data);
    } catch (err) {
      setError('Failed to fetch leaves');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this leave application?')) {
      try {
        await leaveService.deleteLeave(id);
        fetchLeaves();
      } catch (err) {
        alert('Failed to delete leave');
      }
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

  if (loading) return <div className="loading">Loading leaves...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="leave-list-container">
      <div className="page-header">
        <h2>Leave Applications</h2>
        <Link to="/dashboard/leaves/new" className="btn btn-primary btn-apply-leave">
          Apply for Leave
        </Link>
      </div>

      <div className="filters">
        <button
          className={statusFilter === '' ? 'active' : ''}
          onClick={() => setStatusFilter('')}
        >
          All
        </button>
        <button
          className={statusFilter === 'PENDING' ? 'active' : ''}
          onClick={() => setStatusFilter('PENDING')}
        >
          Pending
        </button>
        <button
          className={statusFilter === 'APPROVED' ? 'active' : ''}
          onClick={() => setStatusFilter('APPROVED')}
        >
          Approved
        </button>
        <button
          className={statusFilter === 'REJECTED' ? 'active' : ''}
          onClick={() => setStatusFilter('REJECTED')}
        >
          Rejected
        </button>
      </div>

      <div className="leaves-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Employee</th>
              <th>Type</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Days</th>
              <th>Status</th>
              <th>Applied On</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {leaves.map((leave) => (
              <tr key={leave.id}>
                <td>#{leave.id}</td>
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
                <td>{new Date(leave.appliedDate).toLocaleDateString()}</td>
                <td>
                  <div className="action-buttons">
                    <Link to={`/dashboard/leaves/${leave.id}`} className="btn btn-sm btn-info">
                      View
                    </Link>
                    {/* the employeeId is the id from path */}
                    {(isAdmin() || employee?.id === leave.employeeId) && leave.status === 'PENDING' && (
                      <>
                        <Link to={`/leaves/edit/${leave.id}`} className="btn btn-sm btn-warning">
                          Edit
                        </Link>
                        <button
                          onClick={() => handleDelete(leave.id)}
                          className="btn btn-sm btn-danger"
                        >
                          Delete
                        </button>
                      </>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {leaves.length === 0 && (
        <div className="empty-state">
          <p>No leave applications found</p>
        </div>
      )}
    </div>
  );
};

export default LeaveList;
