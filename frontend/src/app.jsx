import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login"; // Login component yolunu doğru ayarla
import Dashboard from "./pages/Dashboard"; // Dashboard component yolunu doğru ayarla
import { isLoggedIn } from "./services/authService"; // Kullanıcının login durumunu kontrol eden fonksiyon

function App() {
  return (
    <Router>
      <Routes>
        {/* Eğer kullanıcı login değilse dashboard'a erişemez */}
        <Route
          path="/dashboard"
          element={isLoggedIn() ? <Dashboard /> : <Navigate to="/login" />}
        />
        <Route path="/login" element={<Login />} />
        {/* Ana sayfayı login sayfasına yönlendir */}
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;