import React from "react";
import { Routes, Route, useNavigate } from "react-router-dom";

import Login from "./pages/auth/Login";
import Dashboard from "./pages/main/Dashboard";
import PrivateRoute from "./components/PrivateRoute";
import Users from "./pages/Users";
import Profile from "./pages/main/Profile";
import Reports from "./pages/reports/Reports";
import UserActions from "./pages/UserActions";
import { NotificationProvider } from "./pages/Notifications";
import { NotificationList } from "./pages/NotificationList";
import Sidebar from "./layouts/Sidebar";
import { UserProvider, useUser } from "./context/UserContext";
import "./App.css";

function HomePage({ onLogin }) {
  return (
    <div className="app-container">
      <div className="card">
        <h1>LogiSight</h1>
        <p className="tagline">
          Kullanıcı hareketlerini izleme, anomali tespiti, raporlama ve
          sistem verilerinin grafikleştirilmesi
        </p>
        <div className="features">
          <ul>
            <li>Spring Boot & PostgreSQL ile güvenli CRUD Restful API</li>
            <li>SOLID prensiplerine uygun ölçeklenebilir yapı</li>
            <li>Kullanıcı aktivitelerinin anlık takibi</li>
            <li>Anomali tespit algoritmaları</li>
            <li>PDF rapor çıkarma</li>
            <li>Grafik tabanlı veri görselleştirme</li>
          </ul>
        </div>
      </div>
      <div className="login-banner">
        <span>LogiSight sistemine giriş yapmak için tıklayın.</span>
        <div className="banner-actions">
          <button onClick={onLogin}>Giriş Yap</button>
        </div>
      </div>
    </div>
  );
}

// 🔥 Notifications sayfası için ayrı component
function NotificationsPage() {
  const { user } = useUser();
  if (!user) return <div>Yükleniyor...</div>;

  return (
    <div className="dashboard-layout">
      <Sidebar user={user} />
      <main className="main-content">
        <NotificationProvider userId={user.id}>
          <NotificationList />
        </NotificationProvider>
      </main>
    </div>
  );
}

// 🔥 Tüm App’i UserProvider ile sarmaladık
export default function App() {
  const navigate = useNavigate();

  const handleLogin = () => {
    navigate("/login");
  };

  return (
    <UserProvider>
      <Routes>
        <Route path="/" element={<HomePage onLogin={handleLogin} />} />
        <Route path="/login" element={<Login />} />

        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <UserLayout>
                <Dashboard />
              </UserLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/users"
          element={
            <PrivateRoute>
              <UserLayout>
                <Users />
              </UserLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/notifications"
          element={
            <PrivateRoute>
              <NotificationsPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/reports/*"
          element={
            <PrivateRoute>
              <UserLayout>
                <Reports />
              </UserLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/UserActions"
          element={
            <PrivateRoute>
              <UserLayout>
                <UserActions />
              </UserLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <UserLayout>
                <Profile />
              </UserLayout>
            </PrivateRoute>
          }
        />

        <Route path="*" element={<HomePage onLogin={handleLogin} />} />
      </Routes>
    </UserProvider>
  );
}

// 🔥 Sidebar ile ortak layout
function UserLayout({ children }) {
  const { user } = useUser();
  if (!user) return <div>Yükleniyor...</div>;

  return (
    <div className="dashboard-layout">
      <Sidebar user={user} />
      <main className="main-content">{children}</main>
    </div>
  );
}
