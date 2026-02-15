import { useState, useEffect } from 'react';
import { useNavigate, useParams, useLocation, Link } from 'react-router-dom';
import leaveService from '../services/leaveService';
import employeeService from '../services/employeeService';
import { useAuth } from '../context/AuthContext';
import '../styles/Leave.css';

const LeaveForm = () => {
  const navigate = useNavigate();
  const { employee: currentEmployee, isAdmin } = useAuth();

  const { id } = useParams();
  const location = useLocation();
  const isEditMode = Boolean(id);
  const queryParams = new URLSearchParams(location.search);
  const preselectedEmployeeId = queryParams.get('employeeId');

  const [employees, setEmployees] = useState([]);
  const [formData, setFormData] = useState({
    employeeId: preselectedEmployeeId || '',
    leaveType: 'CASUAL',
    startDate: '',
    endDate: '',
    reason: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    // Auto-select current employee if user is not admin and has employee record
    if (!isAdmin() && currentEmployee && !isEditMode) {
      setFormData(prev => (
        { 
          ...prev, 
          employeeId: currentEmployee.id 
        }
      ));
    }
    
    // Only fetch all employees if admin
    if (isAdmin()) {
      fetchEmployees();
    }
    
    if (isEditMode) {
      fetchLeave();
    }
  }, [id, currentEmployee]);

  const fetchEmployees = async () => {
    try {
      const data = await employeeService.getActiveEmployees();
      setEmployees(data);
    } catch (err) {
      setError('Failed to fetch employees. Please try again.');
    }
  };

  const fetchLeave = async () => {
    try {
      const data = await leaveService.getLeaveById(id);
      setFormData({
        employeeId: data.employeeId,
        leaveType: data.leaveType,
        startDate: data.startDate,
        endDate: data.endDate,
        reason: data.reason,
      });
    } catch (err) {
      setError('Failed to fetch leave details');
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isEditMode) {
        await leaveService.updateLeave(id, formData);
      } else {
        await leaveService.applyForLeave(formData);
      }
      navigate('/dashboard/leaves');
    } catch (err) {
      // Handle different error response formats
      let errorMessage = 'Failed to save leave application';
      
      if (err.response?.data) {
        if (typeof err.response.data === 'string') {
          errorMessage = err.response.data;
        } else if (err.response.data.message) {
          errorMessage = err.response.data.message;
        } else if (err.response.data.error) {
          errorMessage = err.response.data.error;
        }
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <div className="form-card">
        <h2>{isEditMode ? 'Edit Leave Application' : 'Apply for Leave'}</h2>
        
        {/* Show error if user doesn't have employee record */}
        {!isAdmin() && !currentEmployee && !isEditMode && (
          <div className="error-message">
            You don't have an employee record yet. Please contact your administrator to create one for you.
          </div>
        )}
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          {/* Show employee dropdown only for admins, hide for regular users */}
          {isAdmin() && (
            <div className="form-group">
              <label htmlFor="employeeId">Employee *</label>
              <select
                id="employeeId"
                name="employeeId"
                value={formData.employeeId}
                onChange={handleChange}
                required
                disabled={isEditMode}
              >
                <option value="">Select Employee</option>
                {employees.map((emp) => (
                  <option key={emp.id} value={emp.id}>
                    {emp.username} - {emp.department}
                  </option>
                ))}
              </select>
            </div>
          )}
          
          {/* Show employee info for regular users */}
          {!isAdmin() && currentEmployee && (
            <div className="form-group">
              <label>Employee</label>
              <div style={{ padding: '12px', backgroundColor: '#f5f7fa', borderRadius: '6px', border: '1px solid #ddd' }}>
                <strong>{currentEmployee.username}</strong> - {currentEmployee.department}
              </div>
            </div>
          )}

          <div className="form-group">
            <label htmlFor="leaveType">Leave Type *</label>
            <select
              id="leaveType"
              name="leaveType"
              value={formData.leaveType}
              onChange={handleChange}
              required
            >
              <option value="SICK">Sick Leave</option>
              <option value="CASUAL">Casual Leave</option>
              <option value="ANNUAL">Annual Leave</option>
              <option value="UNPAID">Unpaid Leave</option>
              <option value="MATERNITY">Maternity Leave</option>
              <option value="PATERNITY">Paternity Leave</option>
              <option value="EMERGENCY">Emergency Leave</option>
            </select>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="startDate">Start Date *</label>
              <input
                type="date"
                id="startDate"
                name="startDate"
                value={formData.startDate}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="endDate">End Date *</label>
              <input
                type="date"
                id="endDate"
                name="endDate"
                value={formData.endDate}
                onChange={handleChange}
                required
                min={formData.startDate}
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="reason">Reason *</label>
            <textarea
              id="reason"
              name="reason"
              value={formData.reason}
              onChange={handleChange}
              required
              rows="4"
              placeholder="Please provide a reason for your leave..."
            />
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate('/dashboard/leaves')}
              className="btn btn-secondary"
            >
              Canceltruthy
            </button>
            <button 
              type="submit" 
              className="btn btn-primary" 
              disabled={loading || (!isAdmin() && !currentEmployee)}
            >
              {loading ? 'Saving...' : isEditMode ? 'Update Leave' : 'Submit Application'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LeaveForm;
