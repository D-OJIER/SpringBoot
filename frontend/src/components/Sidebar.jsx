import { Link } from "react-router-dom";
import {
  Home,
  LayoutDashboard,
  FileText,
  Droplets,
  Receipt,
  Blocks,
  Settings2,
  Building2,
  BarChartIcon,
  Users, // <-- ADDED
} from "lucide-react";

function getUserRole() {
  const token = localStorage.getItem("token");
  if (!token) return null;
  try {
    const payload = token.split(".")[1];
    if (!payload) return null;
    let base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    while (base64.length % 4) base64 += "=";
    const json = atob(base64);
    const data = JSON.parse(json);
    return data.role;
  } catch (e) {
    return null;
  }
}

function Sidebar() {
  const role = getUserRole();
  const isAdmin = role === "ADMIN";

  const logout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <div className="sidebar">
      <h2 className="sidebar__brand">Water System</h2>

      <nav className="sidebar__nav">
        <Link to="/dashboard" className="sidebar__link">
          <LayoutDashboard size={18} />
          Dashboard
        </Link>

        <Link to="/logs" className="sidebar__link">
          <FileText size={18} />
          Daily Logs
        </Link>

        {isAdmin && (
          <>
            <Link to="/sources" className="sidebar__link">
              <Droplets size={18} />
              Water Sources
            </Link>

            <Link to="/rates" className="sidebar__link">
              <Receipt size={18} />
              Water Rates
            </Link>

            <Link to="/apartments" className="sidebar__link">
              <Building2 size={18} />
              Apartments
            </Link>

            <Link to="/apartment-types" className="sidebar__link">
              <Home size={18} />
              Apartment Types
            </Link>

            <Link to="/blocks" className="sidebar__link">
              <Blocks size={18} />
              Blocks
            </Link>

            <Link to="/source-configs" className="sidebar__link">
              <Settings2 size={18} />
              Source Configs
            </Link>

            <Link to="/monthly-summary" className="sidebar__link">
              <BarChartIcon size={18} />
              Reports
            </Link>

            <Link to="/users" className="sidebar__link">
              <Users size={18} />
              Users
            </Link>
          </>
        )}

        <button
          onClick={logout}
          className="button button--danger sidebar__logout"
        >
          Logout
        </button>
      </nav>
    </div>
  );
}

export default Sidebar;

