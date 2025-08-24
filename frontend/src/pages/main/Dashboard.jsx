import React from "react";
import { useNavigate } from "react-router-dom";
import { logout, getToken } from "../../services/authService";
import "./Dashboard.css";

export default function Dashboard() {
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="app-container">
      <div className="card">
        <h1>Dashboard</h1>
        <p>Hoş geldin! Burada sistem istatistiklerini görebilirsin.</p>
        <div style={{ marginTop: 18 }}>
          <button onClick={handleLogout}>Çıkış Yap</button>
        </div>
        <div style={{ marginTop: 12 }}>
          <small>Token: {getToken() ? "Mevcut" : "Yok"}</small>
        </div>
      </div>
    </div>
  );
}