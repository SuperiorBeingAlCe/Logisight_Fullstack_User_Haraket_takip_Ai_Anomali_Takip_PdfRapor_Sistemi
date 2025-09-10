import { NavLink } from "react-router-dom";
import { FaUsers, FaExchangeAlt, FaChartBar, FaBell, FaCog, FaUserCircle, FaHome } from "react-icons/fa";
import "./Sidebar.css";

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">PORTAL</div>
      <nav className="sidebar-menu">
        <NavLink to="/dashboard" className={({ isActive }) => isActive ? "menu-link active" : "menu-link"}>
          <FaHome className="menu-icon" /> <span>Ana Sayfa</span>
        </NavLink>
        <NavLink to="/profile" className={({ isActive }) => isActive ? "menu-link active" : "menu-link"}>
          <FaUserCircle className="menu-icon" /> <span>Profil</span>
        </NavLink>
        <NavLink to="/users" className={({ isActive }) => isActive ? "menu-link active" : "menu-link"}>
          <FaUsers className="menu-icon" /> <span>Kullanıcılar</span>
        </NavLink>
        <NavLink to="/UserActions" className={({ isActive }) => isActive ? "menu-link active" : "menu-link"}>
          <FaExchangeAlt className="menu-icon" /> <span>Hareketler</span>
        </NavLink>
        <NavLink to="/reports" className={({ isActive }) => isActive ? "menu-link active" : "menu-link"}>
          <FaChartBar className="menu-icon" /> <span>Raporlar</span>
        </NavLink>
        <NavLink to="/notifications" className={({ isActive }) => isActive ? "menu-link active" : "menu-link"}>
          <FaBell className="menu-icon" /> <span>Bildirimler</span>
        </NavLink>
        <NavLink to="/SystemConfig" className={({ isActive }) => isActive ? "menu-link active" : "menu-link"}>
          <FaCog className="menu-icon" /> <span>System Config</span>
        </NavLink>
      </nav>
    </aside>
  );
}