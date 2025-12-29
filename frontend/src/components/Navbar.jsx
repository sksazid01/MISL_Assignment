import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Layout.css';

const Navbar = () => {
  const { user, employee, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (err) {
      console.error('Logout failed');
    }
  };

  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          <span className="brand-icon">🏢</span>
          Employee Leave Tracker
        </Link>

        <div className="navbar-menu">
          <Link
            to="/"
            className={`nav-link ${isActive('/') && location.pathname === '/' ? 'active' : ''}`}
          >
            Dashboard
          </Link>
          <Link
            to="/employees"
            className={`nav-link ${isActive('/employees') ? 'active' : ''}`}
          >
            Employees
          </Link>
          {(isAdmin() || employee) && (
            <Link
              to="/leaves"
              className={`nav-link ${isActive('/leaves') ? 'active' : ''}`}
            >
              Leaves
            </Link>
          )}
          {isAdmin() && (
            <Link
              to="/leaves/pending"
              className={`nav-link ${isActive('/leaves/pending') ? 'active' : ''}`}
            >
              Pending Approvals
            </Link>
          )}
        </div>

        <div className="navbar-user">
          <div className="user-info">
            <span className="user-name">{user?.username}</span>
            <span className="user-role">{user?.role}</span>
          </div>
          <button onClick={handleLogout} className="btn btn-logout">
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
