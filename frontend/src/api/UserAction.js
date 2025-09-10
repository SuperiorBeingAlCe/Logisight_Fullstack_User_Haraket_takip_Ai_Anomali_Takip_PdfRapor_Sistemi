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

// 🔧 UserAction oluştur
export async function createUserAction(actionData) {
  const url = `${API_BASE_URL}/user-actions`;
  const res = await axios.post(url, actionData, authHeaders());
  return res.data;
}

// ✏️ UserAction güncelle
export async function updateUserAction(id, actionData) {
  const url = `${API_BASE_URL}/user-actions/${id}`;
  const res = await axios.put(url, actionData, authHeaders());
  return res.data;
}

// ❌ UserAction sil
export async function deleteUserAction(id) {
  const url = `${API_BASE_URL}/user-actions/${id}`;
  await axios.delete(url, authHeaders());
  return true;
}

// 🔎 Tek UserAction getirme
export async function getUserActionById(id) {
  const url = `${API_BASE_URL}/user-actions/${id}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📄 Kullanıcıya göre listeleme (sayfalı)
export async function getUserActionsByUser(userId, start, end, page = 0, size = 10) {
  const url = `${API_BASE_URL}/user-actions/by-user/${userId}?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}&page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📄 IP adresine göre listeleme (sayfalı)
export async function getUserActionsByIp(ipAddress, start, end, page = 0, size = 10) {
  const url = `${API_BASE_URL}/user-actions/by-ip?ipAddress=${encodeURIComponent(ipAddress)}&start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}&page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📄 SessionId ile listeleme (sayfalı)
export async function getUserActionsBySession(sessionId, page = 0, size = 10) {
  const url = `${API_BASE_URL}/user-actions/by-session/${sessionId}?page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📄 Tüm UserAction'ları listele (sayfalı)
export async function getAllUserActions(page = 0, size = 10) {
  const url = `${API_BASE_URL}/user-actions?page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📄 AnomalyId ile listeleme (sayfalı)
export async function getUserActionsByAnomaly(anomalyId, page = 0, size = 10) {
  const url = `${API_BASE_URL}/user-actions/by-anomaly/${anomalyId}?page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📊 Kullanıcının belirli tarihten sonrası için aksiyon sayısı
export async function countUserActionsSince(userId, since) {
  const url = `${API_BASE_URL}/user-actions/count-by-user-since/${userId}?since=${encodeURIComponent(since)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📊 Tüm aksiyonları tiplerine göre say
export async function countActionsGroupedByType() {
  const url = `${API_BASE_URL}/user-actions/count-grouped-by-type`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}