import { API_BASE_URL } from "./index";

export async function login(username, password) {
  const url = `${API_BASE_URL}/auth/login`;

  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });

  if (!res.ok) {
    let errText = "Giriş başarısız";
    try {
      const data = await res.json();
      errText = data?.message || JSON.stringify(data);
    } catch (e) {
      errText = `${res.status} ${res.statusText}`;
    }
    const err = new Error(errText);
    err.response = { data: errText };
    throw err;
  }

  const data = await res.json();

  // Token varsa localStorage'a kaydet
  if (data?.token) localStorage.setItem("token", data.token);
  else if (data?.accessToken) localStorage.setItem("token", data.accessToken);
 if (data?.sessionId) localStorage.setItem("sessionId", data.sessionId);
  return data;
}

export async function logout() {
  const token = localStorage.getItem("token");
  const sessionId = localStorage.getItem("sessionId");
  if (token && sessionId) {
    try {
      await fetch(`${API_BASE_URL}/auth/logout?sessionId=${sessionId}`, {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
      });
    } catch (err) {
      console.error("Logout hatası:", err);
    }
  }
  localStorage.removeItem("token");
  localStorage.removeItem("sessionId");
}

export async function getCurrentUser() {
  const token = getToken();
  if (!token) {
    throw new Error("Token bulunamadı. Kullanıcı giriş yapmamış.");
  }

  const res = await fetch(`${API_BASE_URL}/auth/me`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
    },
  });

  if (!res.ok) {
    if (res.status === 401) {
      throw new Error("Yetkisiz. Tekrar giriş yapın.");
    }
    throw new Error(`Hata: ${res.status} ${res.statusText}`);
  }

  return await res.json(); // Beklenen: UserResponseDto
}

export function getToken() {
  return localStorage.getItem("token");
}

export function isAuthenticated() {
  return !!getToken();
}