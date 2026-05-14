import { Link } from "react-router-dom";
import {
    Home,
    LayoutDashboard,
    FileText,
    Droplets,
    Receipt,
    Blocks,
    Settings2,
    BarChartBigIcon,
    Building2,
    BarChartIcon
} from "lucide-react";

function Sidebar() {

    const logout = () => {

        localStorage.removeItem("token");

        window.location.href = "/login";
    };

    return (
        <div style={{
            width: "250px",
            backgroundColor: "#1a1a1a",
            padding: "20px",
            borderRight: "1px solid #333"
        }}>
            <h2 style={{ marginBottom: "30px" }}>
                Water System
            </h2>

            <nav style={{
                display: "flex",
                flexDirection: "column",
                gap: "15px"
            }}>

                <Link to="/dashboard" style={linkStyle}>
                    <LayoutDashboard size={18} />
                    Dashboard
                </Link>

                <Link to="/logs" style={linkStyle}>
                    <FileText size={18} />
                    Daily Logs
                </Link>

                <Link to="/sources" style={linkStyle}>
                    <Droplets size={18} />
                    Water Sources
                </Link>

                <Link to="/rates" style={linkStyle}>
                    <Receipt size={18} />
                    Water Rates
                </Link>

                <Link to="/apartments" style={linkStyle}>
                    <Building2 size={18} />
                    Apartments
                </Link>

                <Link to="/apartment-types" style={linkStyle}>
                    <Home size={18} />
                    Apartment Types
                </Link>

                <Link to="/blocks" style={linkStyle}>
                    <Blocks size={18} />
                    Blocks
                </Link>

                <Link to="/source-configs" style={linkStyle}>
                    <Settings2 size={18} />
                    Source Configs
                </Link>

                <Link to="/monthly-summary" style={linkStyle}>
                    <BarChartIcon size={18} />
                    Reports
                </Link>
                <button
                    onClick={logout}
                    style={logoutButtonStyle}
                >
                    Logout
                </button>
            </nav>
        </div>
    );
}

const linkStyle = {
    display: "flex",
    alignItems: "center",
    gap: "10px",
    color: "white",
    textDecoration: "none",
    padding: "10px",
    borderRadius: "8px",
    backgroundColor: "#222"
};
const logoutButtonStyle = {
    backgroundColor: "#dc2626",
    color: "white",
    border: "none",
    padding: "10px 14px",
    borderRadius: "8px",
    cursor: "pointer"
};

export default Sidebar;