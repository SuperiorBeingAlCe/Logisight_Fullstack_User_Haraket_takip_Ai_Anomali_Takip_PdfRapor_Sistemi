// Users.jsx (düzeltilmiş)
import React, { useEffect, useState, useCallback, useRef } from "react";
import * as apiUsers from "../api/Users";
import { connectWs, disconnectWs } from "../topic/connectWs";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Pagination,
  Snackbar,
  Alert,
  IconButton,
  InputAdornment,
} from "@mui/material";
import { PersonAdd } from "@mui/icons-material";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";

function Users() {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [openCreate, setOpenCreate] = useState(false);

  const [formCreate, setFormCreate] = useState({
    username: "",
    email: "",
    password: ""
  });

  const [formErrors, setFormErrors] = useState({});
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const pageRef = useRef(page);
  useEffect(() => { pageRef.current = page; }, [page]);

  // format user for table (tolerant)
  const formatUser = (u) => ({
    id: u.id,
    username: u.username || u.userName || u.name || "",
    email: u.email || "",
  
    roles: Array.isArray(u.roles) ? u.roles.join(", ") : (u.roles || ""),
    createdAt: u.createdAt || u.created || null,
    raw: u,
  });

  // load users (page)
  const loadUsers = useCallback(async (p = 0) => {
    try {
      const res = await apiUsers.getAllUsers(p, size);
      const list = (res.content || []).map(formatUser);
      setUsers(list);
      setTotalPages(res.totalPages || 0);
    } catch (err) {
      console.error("getAllUsers error:", err);
      setSnackbar({ open: true, message: "Kullanıcılar yüklenemedi.", severity: "error" });
    }
  }, [size]);

  useEffect(() => { loadUsers(page); }, [loadUsers, page]);

  // WebSocket: real-time güncellemeler
  useEffect(() => {
    const onUserUpdate = (updated) => {
      const u = formatUser(updated);
      setUsers(prev => {
        const idx = prev.findIndex(x => String(x.id) === String(u.id));
        if (idx > -1) {
          const copy = [...prev];
          copy[idx] = u;
          return copy;
        } else {
          if (pageRef.current === 0) {
            const next = [u, ...prev];
            if (next.length > size) next.pop();
            return next;
          }
          return prev;
        }
      });
      setSnackbar({ open: true, message: `Kullanıcı güncellendi: ${u.username || u.email || u.id}`, severity: "info" });
    };

    const onUserDelete = () => {};

    const onUserList = (list) => {
      if (!Array.isArray(list)) return;
      setUsers(list.map(formatUser));
      setSnackbar({ open: true, message: "Kullanıcı listesi alındı (WS).", severity: "success" });
    };

    connectWs(onUserUpdate, onUserDelete, onUserList);
    return () => disconnectWs();
  }, [size]);

  // --- Create dialog handlers ---
  const handleOpenCreate = () => {
    setFormCreate({ username: "", email: "", password: ""});
    setFormErrors({});
    setOpenCreate(true);
  };
  const handleCloseCreate = () => {
    if (!loading) setOpenCreate(false);
  };

  // Basit client-side doğrulama (DTO ile uyumlu)
  const validateForm = () => {
    const errs = {};
    const { username, email, password } = formCreate;

    if (!username || String(username).trim() === "") errs.username = "Kullanıcı adı boş olamaz.";
    else if (username.length < 4) errs.username = "Kullanıcı adı en az 4 karakter olmalı.";
    else if (username.length > 100) errs.username = "Kullanıcı adı 100 karakteri aşamaz.";

    if (!email || String(email).trim() === "") errs.email = "Email boş olamaz.";
    else {
      // basit regex
      const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!re.test(email)) errs.email = "Geçerli bir email girin.";
    }

    if (!password || String(password).trim() === "") errs.password = "Şifre boş olamaz.";
    else if (password.length < 6) errs.password = "Şifre en az 6 karakter olmalı.";

    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const parseServerValidation = (errResp) => {
    // Try several common shapes: { errors: [{field, defaultMessage}], fieldErrors: {...}, violations: [...] }, or {message}
    const out = {};
    if (!errResp) return out;

    if (Array.isArray(errResp.errors)) {
      errResp.errors.forEach(e => {
        if (e.field) out[e.field] = e.defaultMessage || e.message || JSON.stringify(e);
      });
    }
    if (errResp.fieldErrors && typeof errResp.fieldErrors === "object") {
      Object.entries(errResp.fieldErrors).forEach(([k, v]) => out[k] = Array.isArray(v) ? v.join(", ") : String(v));
    }
    if (Array.isArray(errResp.violations)) {
      errResp.violations.forEach(v => { if (v.field) out[v.field] = v.message || v; });
    }
    // some APIs return {username: "...", email: "..."}
    if (typeof errResp === "object") {
      ["username","email","password","roles"].forEach(k => {
        if (errResp[k]) out[k] = Array.isArray(errResp[k]) ? errResp[k].join(", ") : String(errResp[k]);
      });
    }
    return out;
  };

  const handleSaveCreate = async () => {
    if (loading) return;
    if (!validateForm()) return;

    setLoading(true);
    setFormErrors({});
    try {
      // roles: allow user to input comma-separated roles, backend may expect array
      const payload = {
        username: formCreate.username.trim(),
        email: formCreate.email.trim(),
        password: formCreate.password,
       
        // backend may accept roles array or string; prefer array if comma-separated
        roles: formCreate.roles ? formCreate.roles.split(",").map(r => r.trim()).filter(Boolean) : undefined,
      };

      await apiUsers.createUser(payload);

      setSnackbar({ open: true, message: "Kullanıcı oluşturuldu.", severity: "success" });
      handleCloseCreate();
      // reload first page
      await loadUsers(0);
      setPage(0);
    } catch (err) {
      console.error("createUser error:", err);
      const resp = err?.response?.data || err?.response || err?.data || null;
      const serverErrors = parseServerValidation(resp);
      if (Object.keys(serverErrors).length > 0) {
        setFormErrors(serverErrors);
        setSnackbar({ open: true, message: "Formda hatalar var, kontrol ediniz.", severity: "error" });
      } else {
        const message = resp?.message || err?.message || "Kullanıcı oluşturulamadı.";
        setSnackbar({ open: true, message, severity: "error" });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Kullanıcı Yönetimi</h2>

      <div style={{ marginBottom: 10, display: "flex", gap: 8 }}>
        <Button variant="contained" color="primary" startIcon={<PersonAdd />} onClick={handleOpenCreate}>Yeni Kullanıcı</Button>
      </div>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Username</TableCell>
              <TableCell>Email</TableCell>
              
              <TableCell>Roller</TableCell>
              <TableCell>Oluşturulma</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {users.length > 0 ? users.map(u => (
              <TableRow key={u.id}>
                <TableCell>{u.id}</TableCell>
                <TableCell>{u.username || "-"}</TableCell>
                <TableCell>{u.email || "-"}</TableCell>
              
                <TableCell style={{ maxWidth: 200, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{u.roles || "-"}</TableCell>
                <TableCell>{u.createdAt ? new Date(u.createdAt).toLocaleString() : "-"}</TableCell>
              </TableRow>
            )) : (
              <TableRow>
                <TableCell colSpan={6} align="center">Kullanıcı bulunamadı</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <div style={{ marginTop: 10, display: "flex", justifyContent: "center" }}>
        <Pagination
          count={Math.max(1, totalPages)}
          page={page + 1}
          onChange={(e, val) => setPage(val - 1)}
          color="primary"
        />
      </div>

      {/* Create Dialog */}
      <Dialog open={openCreate} onClose={handleCloseCreate}>
        <DialogTitle>Yeni Kullanıcı</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            label="Username"
            fullWidth
            value={formCreate.username}
            onChange={e => setFormCreate({ ...formCreate, username: e.target.value })}
            error={!!formErrors.username}
            helperText={formErrors.username}
            disabled={loading}
          />
          <TextField
            margin="dense"
            label="Email"
            fullWidth
            value={formCreate.email}
            onChange={e => setFormCreate({ ...formCreate, email: e.target.value })}
            error={!!formErrors.email}
            helperText={formErrors.email}
            disabled={loading}
          />
          <TextField
            margin="dense"
            label="Şifre"
            fullWidth
            type={showPassword ? "text" : "password"}
            value={formCreate.password}
            onChange={e => setFormCreate({ ...formCreate, password: e.target.value })}
            error={!!formErrors.password}
            helperText={formErrors.password || "En az 6 karakter"}
            disabled={loading}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={() => setShowPassword(s => !s)} edge="end" size="small">
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              )
            }}
          />
       
        
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseCreate} disabled={loading}>İptal</Button>
          <Button onClick={handleSaveCreate} variant="contained" color="primary" disabled={loading}>
            {loading ? "Kaydediliyor..." : "Kaydet"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar open={snackbar.open} autoHideDuration={3500} onClose={() => setSnackbar({ ...snackbar, open: false })} anchorOrigin={{ vertical: "top", horizontal: "right" }}>
        <Alert severity={snackbar.severity} sx={{ width: "100%" }}>{snackbar.message}</Alert>
      </Snackbar>
    </div>
  );
}

export default Users;
