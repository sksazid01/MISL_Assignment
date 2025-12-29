import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import leaveService from '../services/leaveService';
import { formatDate } from '../utils/dateUtils';
import '../styles/Leave.css';

const PendingLeaves = () => {
  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchPendingLeaves();
  }, []);

  const fetchPendingLeaves = async () => {
    setLoading(true);
    try {
      const data = await leaveService.getPendingLeaves();
      setLeaves(data);
    } catch (err) {
      setError('Failed to fetch pending leaves');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickAction = async (leaveId, action) => {
    const confirmed = window.confirm(
      `Are you sure you want to ${action} this leave application?`
    );
    if (!confirmed) return;

    try {
      if (action === 'approve') {
        await leaveService.updateLeaveStatus(leaveId, { status: 'APPROVED' });
      } else {
        const reason = prompt('Please provide a reason for rejection:');
        if (!reason) return;
        await leaveService.updateLeaveStatus(leaveId, {
          status: 'REJECTED',
          rejectionReason: reason,
        });
      }
      fetchPendingLeaves();
    } catch (err) {
      alert(`Failed to ${action} leave`);
    }
  };

  if (loading) return <div className="loading">Loading pending leaves...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="pending-leaves-container">
      <div className="page-header">
        <h2>Pending Leave Applications</h2>
        <span className="badge">{leaves.length} pending</span>
      </div>

      {leaves.length > 0 ? (
        <div className="pending-leaves-grid">
          {leaves.map((leave) => (
            <div key={leave.id} className="pending-leave-card">
              <div className="card-header">
                <h3>{leave.employeeName}</h3>
                <span className="leave-type">{leave.leaveType}</span>
              </div>
              
              <div className="card-body">
                <div className="info-row">
                  <span className="label">Department:</span>
                  <span className="value">{leave.department}</span>
                </div>
                <div className="info-row">
                  <span className="label">Duration:</span>
                  <span className="value">
                    {formatDate(leave.startDate)} - {formatDate(leave.endDate)}
                  </span>
                </div>
                <div className="info-row">
                  <span className="label">Days:</span>
                  <span className="value">{leave.totalDays} days</span>
                </div>
                <div className="info-row">
                  <span className="label">Applied:</span>
                  <span className="value">
                    {formatDate(leave.appliedDate)}
                  </span>
                </div>
                <div className="reason">
                  <span className="label">Reason:</span>
                  <p>{leave.reason}</p>
                </div>
              </div>

              <div className="card-actions">
                <Link to={`/leaves/${leave.id}`} className="btn btn-info">
                  View Details
                </Link>
                <button
                  onClick={() => handleQuickAction(leave.id, 'approve')}
                  className="btn btn-success"
                >
                  Approve
                </button>
                <button
                  onClick={() => handleQuickAction(leave.id, 'reject')}
                  className="btn btn-danger"
                >
                  Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <p>No pending leave applications</p>
          <Link to="/leaves" className="btn btn-primary">
            View All Leaves
          </Link>
        </div>
      )}
    </div>
  );
};

export default PendingLeaves;
