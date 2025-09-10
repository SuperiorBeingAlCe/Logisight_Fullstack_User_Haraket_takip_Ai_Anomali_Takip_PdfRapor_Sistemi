// src/api/Reports.js
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

// 📄 Report oluştur
export async function createReport(reportData) {
  const url = `${API_BASE_URL}/pdf-reports`;
  const res = await axios.post(url, reportData, authHeaders());
  return res.data;
}

export async function getAllPdfReports(page = 0, size = 10) {
  const url = `${API_BASE_URL}/pdf-reports?page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data; // Page<PdfReportResponseDTO> dönecek
}

// ✏️ Report güncelle
export async function updateReport(id, reportData) {
  const url = `${API_BASE_URL}/pdf-reports/${id}`;
  const res = await axios.put(url, reportData, authHeaders());
  return res.data;
}

// 🔎 Report ID ile getirme
export async function getReportById(id) {
  const url = `${API_BASE_URL}/pdf-reports/${id}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📄 Kullanıcıya göre raporlar
export async function getReportsByUserId(userId, page = 0, size = 10) {
  const url = `${API_BASE_URL}/pdf-reports/by-user/${userId}?page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 🔍 İsme göre arama
export async function searchReportsByName(reportName, page = 0, size = 10) {
  const url = `${API_BASE_URL}/pdf-reports/search?reportName=${encodeURIComponent(reportName)}&page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// 📅 Tarih aralığına göre raporlar
export async function getReportsByDateRange(start, end, page = 0, size = 10) {
  const url = `${API_BASE_URL}/pdf-reports/by-date-range?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}&page=${page}&size=${size}`;
  const res = await axios.get(url, authHeaders());
  return res.data;
}

// ❌ Report sil
export async function deleteReport(id) {
  const url = `${API_BASE_URL}/pdf-reports/${id}`;
  await axios.delete(url, authHeaders());
  return true;
}
