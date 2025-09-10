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

// 📨 Yeni bildirim oluştur
export async function createNotification(createDTO) {
  const url = `${API_BASE_URL}/notifications`;
  const res = await axios.post(url, createDTO, authHeaders());
  return res.data;
}

// 📝 Bildirimi güncelle
export async function updateNotification(id, updateDTO) {
  const url = `${API_BASE_URL}/notifications/${id}`;
  const res = await axios.put(url, updateDTO, authHeaders());
  return res.data;
}

// 🔍 ID ile bildirim getir
export async function getNotificationById(id) {
  const url = `${API_BASE_URL}/notifications/${id}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 👤 Kullanıcıya ait tüm bildirimleri getir
export async function getNotificationsByRecipientId(userId) {
  const url = `${API_BASE_URL}/notifications/by-recipient/${userId}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 👤 Kullanıcıya ait okunmamış bildirimleri getir
export async function getUnreadNotificationsByRecipientId(userId) {
  const url = `${API_BASE_URL}/notifications/unread/by-recipient/${userId}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 👤 Kullanıcıya ait okunmamış bildirim sayısını getir
export async function countUnreadNotificationsByRecipientId(userId) {
  const url = `${API_BASE_URL}/notifications/unread/count/by-recipient/${userId}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// ✅ Bildirimi okunmuş olarak işaretle
export async function markNotificationAsRead(id) {
  const url = `${API_BASE_URL}/notifications/${id}/mark-as-read`;
  await axios.post(url, {}, authHeaders());
}

// ❌ Bildirimi sil
export async function deleteNotification(id) {
  const url = `${API_BASE_URL}/notifications/${id}`;
  await axios.delete(url, authHeaders());
}