import React, { useEffect, useState, useCallback } from "react";
import * as apiActions from "../api/UserAction";
import * as apiUsers from "../api/Users"; // <- kullanıcı getirme işlemleri buradan
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
  IconButton,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Pagination,
  Snackbar,
  Alert,
} from "@mui/material";
import { Edit, Delete } from "@mui/icons-material";

function UserActions() {
  const [actions, setActions] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [openCreate, setOpenCreate] = useState(false);
  const [openUpdate, setOpenUpdate] = useState(false);
  const [editingAction, setEditingAction] = useState(null);

  const [formCreate, setFormCreate] = useState({ userId: "", actionType: "", detail: "" });
  const [formUpdate, setFormUpdate] = useState({ userId: "", actionType: "", detail: "" });

  const [openDetail, setOpenDetail] = useState(false);
  const [detailContent, setDetailContent] = useState("");

  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

  // Kullanıcı önbelleği: { [userId]: userObject }
  const [users, setUsers] = useState({});

  const formatAction = (a) => ({
    id: a.id,
    userId: a.userId,
    userName: undefined, // sonradan set edilecek
    actionType: a.actionType,
    detail: a.actionDetail || "",
    createdAt: a.actionTimestamp || a.createdAt || null,
  });

  const getDisplayName = (user) => {
    if (!user) return null;
    // API'den gelecek farklı alan isimlerine esnek yaklaşım
    return user.username || user.userName || user.name || user.fullName || user.displayName || user.email || String(user.id);
  };

  // Bir kullanıcıyı getir ve önbelleğe al; varsa tekrar istek atılmaz
  const fetchUserIfNeeded = useCallback(async (id) => {
    if (!id) return null;
    if (users[id]) return users[id];
    try {
      const u = await apiUsers.getUserById(id);
      setUsers(prev => ({ ...prev, [id]: u }));
      // actions içindeki ilgili satırı güncelle
      setActions(prev => prev.map(a => a.userId === id ? { ...a, userName: getDisplayName(u) } : a));
      return u;
    } catch (err) {
      // hata durumunda önbelleğe null koyabiliriz (tek seferlik deneme)
      setUsers(prev => ({ ...prev, [id]: null }));
      return null;
    }
  }, [users]);

  // Tüm actions için eksik kullanıcıları getir
  const fetchUsersForActions = useCallback(async (actionsList) => {
    const ids = Array.from(new Set((actionsList || []).map(a => a.userId).filter(Boolean)));
    const toFetch = ids.filter(id => !(id in users));
    if (toFetch.length === 0) {
      // varsa mevcut kullanıcı isimlerini actionlara uygula
      setActions(prev => prev.map(a => ({ ...a, userName: getDisplayName(users[a.userId]) || a.userName })));
      return;
    }
    try {
      await Promise.all(toFetch.map(id => fetchUserIfNeeded(id)));
    } catch (e) {
      // ignore, hata snackbar gösterilebilir ama isteğe bağlı
    }
  }, [users, fetchUserIfNeeded]);

  const loadActions = useCallback(async (p = 0) => {
    try {
      const res = await apiActions.getAllUserActions(p, size);
      const formatted = (res.content || []).map(formatAction);
      setActions(formatted);
      setTotalPages(res.totalPages || 0);
      // eksik kullanıcı bilgilerini getir
      fetchUsersForActions(formatted);
    } catch (err) {
      setSnackbar({ open: true, message: "Liste yüklenemedi.", severity: "error" });
    }
  }, [size, fetchUsersForActions]);

  useEffect(() => { loadActions(page); }, [loadActions, page]);

  useEffect(() => {
    const onActionUpdate = async (updated) => {
      const mapped = formatAction(updated);
      // önce kullanıcı adını mümkünse önbellekten al veya getir
      const cached = users[mapped.userId];
      if (cached !== undefined) {
        mapped.userName = getDisplayName(cached);
      } else {
        // fetch et (istemci tarafında güncelleme yapacak)
        fetchUserIfNeeded(mapped.userId).then(u => {
          mapped.userName = getDisplayName(u);
          // state güncellemesi: ekle veya replace
          setActions(prev => {
            const idx = prev.findIndex(x => x.id === mapped.id);
            if (idx > -1) {
              const copy = [...prev]; copy[idx] = mapped; return copy;
            } else {
              return [mapped, ...prev].slice(0, size);
            }
          });
        }).catch(() => {
          // hata olursa yine de listeyi güncelle
          setActions(prev => {
            const idx = prev.findIndex(x => x.id === mapped.id);
            if (idx > -1) {
              const copy = [...prev]; copy[idx] = mapped; return copy;
            } else {
              return [mapped, ...prev].slice(0, size);
            }
          });
        });
        return;
      }

      setActions(prev => {
        const idx = prev.findIndex(x => x.id === mapped.id);
        if (idx > -1) {
          const copy = [...prev];
          copy[idx] = mapped;
          return copy;
        } else {
          return [mapped, ...prev].slice(0, size);
        }
      });
    };

    const onActionDelete = (deletedId) => {
      setActions(prev => prev.filter(a => a.id !== deletedId));
    };

    connectWs(onActionUpdate, onActionDelete);
    return () => disconnectWs();
  }, [size, users, fetchUserIfNeeded]);

  const handleOpenCreate = () => { setFormCreate({ userId: "", actionType: "", detail: "" }); setOpenCreate(true); };
  const handleCloseCreate = () => setOpenCreate(false);

  const handleOpenUpdate = (action) => {
    setEditingAction(action);
    setFormUpdate({ userId: action.userId, actionType: action.actionType, detail: action.detail });
    setOpenUpdate(true);
  };
  const handleCloseUpdate = () => { setEditingAction(null); setOpenUpdate(false); };

  const handleSaveCreate = async () => {
    try {
      await apiActions.createUserAction({ userId: formCreate.userId, actionType: formCreate.actionType, actionDetail: formCreate.detail || "" });
      setSnackbar({ open: true, message: "Action oluşturuldu.", severity: "success" });
      handleCloseCreate();
      loadActions(0);
    } catch (err) {
      setSnackbar({ open: true, message: err?.response?.data?.message || "Kayıt başarısız.", severity: "error" });
    }
  };

  const handleSaveUpdate = async () => {
    try {
      await apiActions.updateUserAction(editingAction.id, { userId: formUpdate.userId, actionType: formUpdate.actionType, actionDetail: formUpdate.detail || "" });
      setSnackbar({ open: true, message: "Action güncellendi.", severity: "success" });
      handleCloseUpdate();
      loadActions(page);
    } catch (err) {
      setSnackbar({ open: true, message: err?.response?.data?.message || "Güncelleme başarısız.", severity: "error" });
    }
  };

  const handleDelete = async (id) => {
    try {
      await apiActions.deleteUserAction(id);
      setActions(prev => prev.filter(a => a.id !== id));
      setSnackbar({ open: true, message: "Action silindi.", severity: "success" });
    } catch (err) {
      setSnackbar({ open: true, message: err?.response?.data?.message || "Silme başarısız.", severity: "error" });
    }
  };

  const handleOpenDetail = (d) => { setDetailContent(d || "Detay yok"); setOpenDetail(true); };
  const handleCloseDetail = () => setOpenDetail(false);

  return (
    <div style={{ padding: 20 }}>
      <h2>User Actions Yönetimi</h2>
      <Button variant="contained" color="primary" onClick={handleOpenCreate} style={{ marginBottom: 10 }}>Yeni Action</Button>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>User</TableCell>
              <TableCell>Action Type</TableCell>
              <TableCell style={{ width: 400 }}>Detail</TableCell>
              <TableCell>Tarih</TableCell>
              <TableCell>İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {actions.length > 0 ? actions.map(a => {
              const isLong = (a.detail || "").length > 20;
              // Eğer userName yoksa userId göster (geçici)
              const userLabel = a.userName || a.userId || <i>- bilinmiyor -</i>;
              return (
                <TableRow key={a.id}>
                  <TableCell>{a.id}</TableCell>
                  <TableCell title={String(a.userId)}>{userLabel}</TableCell>
                  <TableCell>{a.actionType}</TableCell>

                  <TableCell>
                    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                      <div style={{ flex: 1, minWidth: 0 }}>
                        {!isLong ? (
                          <span>{a.detail || <i>- boş -</i>}</span>
                        ) : (
                          <span style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap", display: "block" }}>
                            {a.detail.slice(0, 20) + "..."}
                          </span>
                        )}
                      </div>

                      {isLong && (
                        <Button size="small" onClick={() => handleOpenDetail(a.detail)}>Detay</Button>
                      )}
                    </div>
                  </TableCell>

                  <TableCell>{a.createdAt ? new Date(a.createdAt).toLocaleString() : "-"}</TableCell>
                  <TableCell>
                    <IconButton color="primary" onClick={() => handleOpenUpdate(a)} title="Düzenle"><Edit /></IconButton>
                    <IconButton color="error" onClick={() => handleDelete(a.id)} title="Sil"><Delete /></IconButton>
                  </TableCell>
                </TableRow>
              );
            }) : (
              <TableRow>
                <TableCell colSpan={6} align="center">Action bulunamadı</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <div style={{ marginTop: 10, display: "flex", justifyContent: "center" }}>
        <Pagination count={Math.max(1, totalPages)} page={page + 1} onChange={(e, val) => setPage(val - 1)} color="primary" />
      </div>

      <Dialog open={openCreate} onClose={handleCloseCreate}>
        <DialogTitle>Yeni Action</DialogTitle>
        <DialogContent>
          <TextField margin="dense" label="User ID" fullWidth value={formCreate.userId} onChange={e => setFormCreate({ ...formCreate, userId: e.target.value })} />
          <TextField margin="dense" label="Action Type" fullWidth value={formCreate.actionType} onChange={e => setFormCreate({ ...formCreate, actionType: e.target.value })} />
          <TextField margin="dense" label="Detail" fullWidth value={formCreate.detail} onChange={e => setFormCreate({ ...formCreate, detail: e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseCreate}>İptal</Button>
          <Button onClick={handleSaveCreate} variant="contained" color="primary">Kaydet</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openUpdate} onClose={handleCloseUpdate}>
        <DialogTitle>Action Güncelle</DialogTitle>
        <DialogContent>
          <TextField margin="dense" label="User ID" fullWidth value={formUpdate.userId} onChange={e => setFormUpdate({ ...formUpdate, userId: e.target.value })} />
          <TextField margin="dense" label="Action Type" fullWidth value={formUpdate.actionType} onChange={e => setFormUpdate({ ...formUpdate, actionType: e.target.value })} />
          <TextField margin="dense" label="Detail" fullWidth value={formUpdate.detail} onChange={e => setFormUpdate({ ...formUpdate, detail: e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseUpdate}>İptal</Button>
          <Button onClick={handleSaveUpdate} variant="contained" color="primary">Güncelle</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openDetail} onClose={handleCloseDetail} maxWidth="md" fullWidth>
        <DialogTitle>Detay</DialogTitle>
        <DialogContent>
          <div style={{ whiteSpace: "pre-wrap", fontFamily: "monospace", fontSize: 13 }}>{detailContent}</div>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDetail}>Kapat</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snackbar.open} autoHideDuration={3000} onClose={() => setSnackbar({ ...snackbar, open: false })} anchorOrigin={{ vertical: "top", horizontal: "right" }}>
        <Alert severity={snackbar.severity} sx={{ width: "100%" }}>{snackbar.message}</Alert>
      </Snackbar>
    </div>
  );
}

export default UserActions;
