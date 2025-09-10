import { API_BASE_URL } from "./index";
import { getToken } from "./Auth";
import axios from "axios";

// 🔐 Authorization header'ını hazırlar
function authHeaders() {
  const token = getToken();
  if (!token) {
    throw new Error("Token bulunamadı, login olun.");
  }
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  };
}

// 🔧 Kullanıcı oluştur
export async function createUser(userData) {
  const url = `${API_BASE_URL}/users/save`;
  const res = await axios.post(url, userData, authHeaders());
  return res.data;
}

// ✏️ Kullanıcı güncelle
export async function updateUser(id, userData) {
  const url = `${API_BASE_URL}/users/${id}`;
  const res = await axios.put(url, userData, authHeaders());
  return res.data;
}

// ❌ Kullanıcı sil
export async function deleteUser(id) {
  const url = `${API_BASE_URL}/users/${id}`;
  await axios.delete(url, authHeaders());
  return true;
}

// 🔁 Kullanıcı aktif/pasif yap
export async function setUserEnabled(id, enabled) {
  const url = `${API_BASE_URL}/users/${id}/enabled?enabled=${enabled}`;
  const res = await axios.patch(url, {}, authHeaders());
  return res.data;
}

// 🔎 Kullanıcı getirme işlemleri
export async function getUserById(id) {
  const url = `${API_BASE_URL}/users/${id}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

export async function getUserByUsername(username) {
  const url = `${API_BASE_URL}/users/by-username?username=${encodeURIComponent(username)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

export async function getUserByEmail(email) {
  const url = `${API_BASE_URL}/users/by-email?email=${encodeURIComponent(email)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📄 Listeleme (sayfalı)
export async function getEnabledUsers(page = 0, size = 10) {
  const url = `${API_BASE_URL}/users/enabled?page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

export async function getAllUsers(page = 0, size = 10) {
  const url = `${API_BASE_URL}/users?page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// ✅ Kontrol (var mı yok mu)
export async function existsByUsername(username) {
  const url = `${API_BASE_URL}/users/exists/username/${encodeURIComponent(username)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

export async function existsByEmail(email) {
  const url = `${API_BASE_URL}/users/exists/email/${encodeURIComponent(email)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}