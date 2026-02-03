import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import leaveService from '../services/leaveService';
import { useAuth } from '../context/AuthContext';
import { formatDate } from '../utils/dateUtils';
import '../styles/Leave.css';

const LeaveDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [leave, setLeave] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [rejectionReason, setRejectionReason] = useState('');
  const [showRejectModal, setShowRejectModal] = useState(false);

  useEffect(() => {
    fetchLeave();
  }, [id]);

  const fetchLeave = async () => {
    setLoading(true);
    try {
      const data = await leaveService.getLeaveById(id);
      setLeave(data);
    } catch (err) {
      setError('Failed to fetch leave details');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async () => {
    if (!window.confirm('Are you sure you want to approve this leave?')) return;
    
    setActionLoading(true);
    try {
      await leaveService.updateLeaveStatus(id, { status: 'APPROVED' });
      fetchLeave();
    } catch (err) {
      alert('Failed to approve leave');
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!rejectionReason.trim()) {
      alert('Please provide a reason for rejection');
      return;
    }

    setActionLoading(true);
    try {
      await leaveService.updateLeaveStatus(id, {
        status: 'REJECTED',
        rejectionReason,
      });
      setShowRejectModal(false);
      fetchLeave();
    } catch (err) {
      alert('Failed to reject leave');
    } finally {
      setActionLoading(false);
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

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!leave) return <div className="error-message">Leave not found</div>;

  return (
    <div className="leave-details-container">
      <div className="page-header">
        <h2>Leave Application Details</h2>
        <button onClick={() => navigate('/leaves')} className="btn btn-secondary">
          Back to List
        </button>
      </div>

      <div className="details-card">
        <div className="detail-header">
          <h3>Leave ID: {leave.id}</h3>
          <span className={`status-badge ${getStatusColor(leave.status)}`}>
            {leave.status}
          </span>
        </div>

        <div className="details-grid">
          <div className="info-section">
            <h4>Employee Information</h4>
            <div className="info-row">
              <span className="label">Name:</span>
              <span className="value">{leave.employeeName}</span>
            </div>
            <div className="info-row">
              <span className="label">Department:</span>
              <span className="value">{leave.department}</span>
            </div>
          </div>

          <div className="info-section">
            <h4>Leave Details</h4>
            <div className="info-row">
              <span className="label">Type:</span>
              <span className="value">{leave.leaveType}</span>
            </div>
            <div className="info-row">
              <span className="label">Start Date:</span>
              <span className="value">{new Date(leave.startDate).toLocaleDateString()}</span>
            </div>
            <div className="info-row">
              <span className="label">End Date:</span>
              <span className="value">{new Date(leave.endDate).toLocaleDateString()}</span>
            </div>
            <div className="info-row">
              <span className="label">Number of Days:</span>
              <span className="value">{leave.totalDays}</span>
            </div>
          </div>

          <div className="info-section">
            <h4>Application Status</h4>
            <div className="info-row">
              <span className="label">Applied On:</span>
              <span className="value">{new Date(leave.appliedDate).toLocaleDateString()}</span>
            </div>
            {leave.approvalDate && (
              <>
                <div className="info-row">
                  <span className="label">Approved On:</span>
                  <span className="value">{new Date(leave.approvalDate).toLocaleDateString()}</span>
                </div>
                <div className="info-row">
                  <span className="label">Approved By:</span>
                  <span className="value">{leave.approvedBy}</span>
                </div>
              </>
            )}
            {leave.status === 'REJECTED' && leave.rejectionReason && (
              <div className="info-row">
                <span className="label">Rejection Reason:</span>
                <span className="value" style={{ color: '#d32f2f' }}>{leave.rejectionReason}</span>
              </div>
            )}
          </div>
        </div>

        <div className="reason-section">
          <h4>Reason for Leave</h4>
          <p>{leave.reason}</p>
        </div>

        {leave.rejectionReason && (
          <div className="rejection-section">
            <h4>Rejection Reason</h4>
            <p>{leave.rejectionReason}</p>
          </div>
        )}

        {isAdmin() && leave.status === 'PENDING' && (
          <div className="action-buttons">
            <button
              onClick={handleApprove}
              className="btn btn-success"
              disabled={actionLoading}
            >
              {actionLoading ? 'Processing...' : 'Approve Leave'}
            </button>
            <button
              onClick={() => setShowRejectModal(true)}
              className="btn btn-danger"
              disabled={actionLoading}
            >
              Reject Leave
            </button>
          </div>
        )}
      </div>

      {showRejectModal && (
        <div className="modal-overlay" onClick={() => setShowRejectModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Reject Leave Application</h3>
            <div className="form-group">
              <label>Reason for Rejection *</label>
              <textarea
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
                rows="4"
                placeholder="Please provide a reason for rejection..."
              />
            </div>
            <div className="modal-actions">
              <button
                onClick={() => setShowRejectModal(false)}
                className="btn btn-secondary"
                disabled={actionLoading}
              >
                Cancel
              </button>
              <button
                onClick={handleReject}
                className="btn btn-danger"
                disabled={actionLoading}
              >
                {actionLoading ? 'Rejecting...' : 'Confirm Rejection'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default LeaveDetails;
