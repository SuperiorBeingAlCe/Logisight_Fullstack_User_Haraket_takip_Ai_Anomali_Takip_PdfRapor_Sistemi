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

// 📊 Günlük login istatistikleri getir
export async function getDailyLogins(start, end) {
  const url = `${API_BASE_URL}/stats/logins?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📊 Günlük rapor istatistikleri getir
export async function getDailyReports(start, end) {
  const url = `${API_BASE_URL}/stats/reports?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📊 Özet istatistikler getir (toplam user, aktif user, toplam rapor)
export async function getSummaryStats() {
  const url = `${API_BASE_URL}/stats/summary`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📊 Period-based login istatistikleri getir (daily / weekly / monthly)
export async function getLoginsByPeriod(period, start, end) {
  const url = `${API_BASE_URL}/stats/logins/period?period=${encodeURIComponent(period)}&start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}
