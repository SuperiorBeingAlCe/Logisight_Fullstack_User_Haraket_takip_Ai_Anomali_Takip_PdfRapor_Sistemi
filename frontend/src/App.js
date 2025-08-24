import React, { useState, useEffect } from "react";
import { Routes, Route, useNavigate } from "react-router-dom";

// <-- burada yol düzeltildi:
import Login from "./pages/auth/Login";
import Dashboard from "./pages/main/Dashboard";

import PrivateRoute from "./components/PrivateRoute";
import "./App.css";

function HomePage({ onLogin, onClose, showBanner }) {
  return (
    <div className="app-container">
      <div className="card">
        <h1>LogiSight</h1>
        <p className="tagline">
          Kullanıcı hareketlerini tespit eden akıllı anomali tespit sistemi
        </p>

        <section className="description">
          <h2>Proje Amacı</h2>
          <p>
            LogiSight, sistem içindeki her kullanıcı hareketini tespit eder,
            anomali durumlarını belirler ve detaylı PDF raporlar üretir. Role
            hiyerarşisi, bildirim yönetimi ve anlık gözlemleme yetenekleriyle
            tam kontrol sağlar.
          </p>
        </section>

        <section className="features">
          <h2>Öne Çıkan Özellikler</h2>
          <ul>
            <li>Kullanıcı hareket tespiti ve gözlemleme</li>
            <li>Anomali analizi ve PDF raporlama</li>
            <li>Role hiyerarşisi ve bildirim sistemi</li>
            <li>PostgreSQL veritabanı</li>
          </ul>
        </section>

        <section className="backend">
          <h2>Backend Teknolojileri</h2>
          <p>
            Spring Boot tabanlı yapı: Entity, DTO, Repository, Service,
            Controller, Security & JWT, Mapper, Scheduler, Custom & Global
            Exception, WebSocket, Service Cache yönetimi, Pageable, Aspect
            (kullanıcı hareket takibi için custom annotation).
          </p>
        </section>

        <section className="frontend">
          <h2>Frontend Teknolojileri</h2>
          <p>React & JavaScript tabanlı modern kullanıcı arayüzü.</p>
        </section>

        <section className="docs">
          <h2>API Dokümantasyonu</h2>
          <p>Postman ile hazırlanmış API dokümantasyonu mevcut.</p>
        </section>
      </div>

      {showBanner && (
        <div className="login-banner">
          <span>LogiSight sistemine giriş yapmak için tıklayın.</span>
          <div className="banner-actions">
            <button onClick={onLogin}>Giriş Yap</button>
            <span className="close-btn" onClick={onClose}>
              ×
            </span>
          </div>
        </div>
      )}
    </div>
  );
}

export default function App() {
  const [showBanner, setShowBanner] = useState(
    localStorage.getItem("bannerClosed") !== "true"
  );
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.setItem("bannerClosed", showBanner ? "false" : "true");
  }, [showBanner]);

  const handleLogin = () => {
    navigate("/login");
  };

  const handleClose = () => {
    setShowBanner(false);
  };

  return (
    <Routes>
      <Route
        path="/"
        element={
          <HomePage
            onLogin={handleLogin}
            onClose={handleClose}
            showBanner={showBanner}
          />
        }
      />
      <Route path="/login" element={<Login />} />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        }
      />
      <Route
        path="*"
        element={
          <HomePage
            onLogin={handleLogin}
            onClose={handleClose}
            showBanner={showBanner}
          />
        }
      />
    </Routes>
  );
}