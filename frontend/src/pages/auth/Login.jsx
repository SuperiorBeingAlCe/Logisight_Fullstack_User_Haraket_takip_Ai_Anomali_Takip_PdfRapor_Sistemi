import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login as loginApi } from "../../api/Auth";
import { useUser } from "../../context/UserContext"; // 🔥
import "./Login.css";

export default function Login() {
  const navigate = useNavigate();
  const { setUser } = useUser(); // 🔥
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const userData = await loginApi(username, password); // 🔥 login API’den user döndürmeli
      setUser(userData); // global context’e kaydet
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