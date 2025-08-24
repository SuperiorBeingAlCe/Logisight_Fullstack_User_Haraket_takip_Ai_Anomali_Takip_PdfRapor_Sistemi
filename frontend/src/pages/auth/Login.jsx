import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../../services/authService";
import "./Login.css";

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await login(username, password);
      navigate("/dashboard");
    } catch (err) {
      const errMsg =
        (err && err.message) ||
        (err?.response?.data?.message) ||
        "Kullanıcı adı veya şifre hatalı";
      setError(errMsg);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>Oturum Aç</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Kullanıcı Adı"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
          <input
            type="password"
            placeholder="Şifre"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
          <button type="submit">Giriş Yap</button>
          {error && <p className="error-msg">{error}</p>}
        </form>
      </div>
    </div>
  );
}