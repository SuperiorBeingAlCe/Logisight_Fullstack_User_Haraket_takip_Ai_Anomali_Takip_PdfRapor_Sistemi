// src/pages/main/Profile.jsx
import React from "react";
import { useCurrentUser } from "../../hook/useCurrentUser";
import "./profile.css";

const Profile = () => {
  const [user] = useCurrentUser();

  if (!user) return <p className="loading">Durum yükleniyor...</p>;

  return (
    <div className="profile-card">
      <div className="avatar">
        {/* Avatar resmi koymak istersen buraya ekleyebilirsin */}
      </div>
      <div className="user-info">
        <h2>{user.username}</h2>
        <p><strong>ID:</strong> {user.id}</p>
        <p><strong>Email:</strong> {user.email}</p>
        <p><strong>Oluşturulma:</strong> {new Date(user.createdAt).toLocaleString()}</p>

        {/* Roller */}
        <p>
          <strong>Roller:</strong>{" "}
          {Array.isArray(user.roles) 
            ? user.roles.join(", ") 
            : (user.roles || "-")}
        </p>

        {/* Aktiflik durumu */}
        <span className={user.enabled ? "status-active" : "status-inactive"}>
          {user.enabled ? "Aktif" : "Pasif"}
        </span>
      </div>
    </div>
  );
};

export default Profile;
