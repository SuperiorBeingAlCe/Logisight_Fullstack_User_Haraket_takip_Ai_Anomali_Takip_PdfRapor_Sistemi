import React from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";
import { logout, getToken } from "../../api/Auth";
export default function Dashboard() {
  const navigate = useNavigate();

  const handleLogout = async () => {  
  await logout(getToken());
  navigate("/login");
};

  return (
    <div className="card">
      <h1>Dashboard</h1>
      <p>Hoş geldin! Burada sistem istatistiklerini görebilirsin.</p>
      <button onClick={handleLogout}>Çıkış Yap</button>
      <div style={{ marginTop: 12 }}>
        <small>Token: {getToken() ? "Mevcut" : "Yok"}</small>
      </div>
    </div>
  );
}