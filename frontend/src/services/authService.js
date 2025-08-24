const API = "https://localhost:8080";

export async function login(username, password) {
  const url = `${API}/api/auth/login`;

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

  return data;
}

export function logout() {
  localStorage.removeItem("token");
}

export function getToken() {
  return localStorage.getItem("token");
}

export function isAuthenticated() {
  return !!getToken();
}