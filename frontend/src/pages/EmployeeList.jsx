import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import employeeService from '../services/employeeService';
import { useAuth } from '../context/AuthContext';
import { formatDate } from '../utils/dateUtils';
import '../styles/Employee.css';

const EmployeeList = () => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('all'); // all, active, department
  const [selectedDepartment, setSelectedDepartment] = useState('');
  const { isAdmin } = useAuth();

  useEffect(() => {
    fetchEmployees();
  }, [filter, selectedDepartment]);

  const fetchEmployees = async () => {
    setLoading(true);
    setError('');
    try {
      let data;
      if (filter === 'active') {
        data = await employeeService.getActiveEmployees();
      } else if (filter === 'department' && selectedDepartment) {
        data = await employeeService.getEmployeesByDepartment(selectedDepartment);
      } else {
        data = await employeeService.getAllEmployees();
      }
      setEmployees(data);
    } catch (err) {
      setError('Failed to fetch employees');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this employee?')) {
      try {
        await employeeService.deleteEmployee(id);
        fetchEmployees();
      } catch (err) {
        alert('Failed to delete employee');
      }
    }
  };

  const handleToggleActive = async (id, isActive) => {
    try {
      if (isActive) {
        await employeeService.deactivateEmployee(id);
      } else {
        await employeeService.activateEmployee(id);
      }
      fetchEmployees();
    } catch (err) {
      alert('Failed to update employee status');
    }
  };

  if (loading) return <div className="loading">Loading employees...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="employee-list-container">
      <div className="page-header">
        <h2>Employees</h2>
        {isAdmin() && (
          <Link to="/employees/new" className="btn-primary">
            Add Employee
          </Link>
        )}
      </div>

      <div className="filters">
        <button
          className={filter === 'all' ? 'active' : ''}
          onClick={() => setFilter('all')}
        >
          All
        </button>
        <button
          className={filter === 'active' ? 'active' : ''}
          onClick={() => setFilter('active')}
        >
          Active Only
        </button>
        <div className="department-filter">
          <select
            value={selectedDepartment}
            onChange={(e) => {
              setSelectedDepartment(e.target.value);
              setFilter('department');
            }}
          >
            <option value="">All Department</option>
            <option value="Engineering">Engineering</option>
            <option value="HR">HR</option>
            <option value="Finance">Finance</option>
            <option value="Marketing">Marketing</option>
            <option value="Sales">Sales</option>
          </select>
        </div>
      </div>

      <div className="employee-grid">
        {employees.map((employee) => (
          <div key={employee.id} className="employee-card">
            <div className="employee-header">
              <h3>{employee.username}</h3>
              <span className={`status-badge ${employee.isActive ? 'active' : 'inactive'}`}>
                {employee.isActive ? 'Active' : 'Inactive'}
              </span>
            </div>
            <div className="employee-details">
              <p><strong>Email:</strong> {employee.email}</p>
              <p><strong>Department:</strong> {employee.department}</p>
              <p><strong>Designation:</strong> {employee.designation}</p>
              <p><strong>Phone:</strong> {employee.phoneNumber}</p>
              <p><strong>Join Date:</strong> {formatDate(employee.joinDate)}</p>
            </div>
            <div className="employee-actions">
              <Link to={`/employees/${employee.id}`} className="btn btn-secondary">
                View Details
              </Link>
              {isAdmin() && (
                <>
                  <Link to={`/employees/edit/${employee.id}`} className="btn btn-info">
                    Edit
                  </Link>
                  <button
                    onClick={() => handleToggleActive(employee.id, employee.isActive)}
                    className="btn btn-warning"
                  >
                    {employee.isActive ? 'Deactivate' : 'Activate'}
                  </button>
                  <button
                    onClick={() => handleDelete(employee.id)}
                    className="btn btn-danger"
                  >
                    Delete
                  </button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>

      {employees.length === 0 && (
        <div className="empty-state">
          <p>No employees found</p>
        </div>
      )}
    </div>
  );
};

export default EmployeeList;
