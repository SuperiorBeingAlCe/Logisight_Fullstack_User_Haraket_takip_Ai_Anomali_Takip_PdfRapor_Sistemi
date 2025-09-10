// Reports.jsx
import React, { useEffect, useState, useCallback, useRef } from "react";
import * as apiReports from "../../api/Reports";
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
} from "@mui/material";
import { PictureAsPdf } from "@mui/icons-material";

function ReportsTab() {
  const [reports, setReports] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [openCreate, setOpenCreate] = useState(false);
  const [formCreate, setFormCreate] = useState({ reportName: "", generatedByUserId: "" });

  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

  const pageRef = useRef(page);
  useEffect(() => { pageRef.current = page; }, [page]);

  // load reports (page)
  const loadReports = useCallback(async (p = 0) => {
    try {
      const res = await apiReports.getAllPdfReports(p, size);
      setReports(res.content || []);
      setTotalPages(res.totalPages || 0);
    } catch (err) {
      console.error("getAllPdfReports error:", err);
      setSnackbar({ open: true, message: "Raporlar yüklenemedi.", severity: "error" });
    }
  }, [size]);

  useEffect(() => { loadReports(page); }, [loadReports, page]);

  // Create dialog
  const handleOpenCreate = () => {
    setFormCreate({ reportName: "", generatedByUserId: "" });
    setOpenCreate(true);
  };
  const handleCloseCreate = () => setOpenCreate(false);

  const handleSaveCreate = async () => {
    try {
      const payload = {
        reportName: formCreate.reportName,
        generatedByUserId: formCreate.generatedByUserId || null,
        start: null, // istersen tarih aralığı eklenebilir
        end: null,
        type: "DEFAULT", // örnek type
      };
      await apiReports.createReport(payload);
      setSnackbar({ open: true, message: "Rapor oluşturuldu.", severity: "success" });
      handleCloseCreate();
      loadReports(0);
      setPage(0);
    } catch (err) {
      console.error("createReport error:", err);
      setSnackbar({ open: true, message: err?.response?.data?.message || "Rapor oluşturulamadı.", severity: "error" });
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>PDF Raporlar</h2>

      <div style={{ marginBottom: 10 }}>
        <Button variant="contained" color="primary" startIcon={<PictureAsPdf />} onClick={handleOpenCreate}>
          Yeni Rapor
        </Button>
      </div>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Rapor Adı</TableCell>
              <TableCell>Oluşturulma</TableCell>
              <TableCell>Oluşturan Kullanıcı ID</TableCell>
              <TableCell>Oluşturan Kullanıcı Adı</TableCell>
              <TableCell>Dosya Boyutu (Bytes)</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {reports.length > 0 ? reports.map(r => (
              <TableRow key={r.id || r.filePath}>
                <TableCell>{r.id || "-"}</TableCell>
                <TableCell>{r.reportName || "-"}</TableCell>
                <TableCell>{r.generatedAt ? new Date(r.generatedAt).toLocaleString() : "-"}</TableCell>
                <TableCell>{r.generatedByUserId || "-"}</TableCell>
                <TableCell>{r.generatedByUsername || "-"}</TableCell>
                <TableCell>{r.fileSizeBytes || "-"}</TableCell>
              </TableRow>
            )) : (
              <TableRow>
                <TableCell colSpan={6} align="center">Rapor bulunamadı</TableCell>
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
        <DialogTitle>Yeni PDF Rapor</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            label="Rapor Adı"
            fullWidth
            value={formCreate.reportName}
            onChange={e => setFormCreate({ ...formCreate, reportName: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Oluşturan Kullanıcı ID"
            fullWidth
            value={formCreate.generatedByUserId}
            onChange={e => setFormCreate({ ...formCreate, generatedByUserId: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseCreate}>İptal</Button>
          <Button onClick={handleSaveCreate} variant="contained" color="primary">Kaydet</Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3500}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert severity={snackbar.severity} sx={{ width: "100%" }}>{snackbar.message}</Alert>
      </Snackbar>
    </div>
  );
}

export default ReportsTab;
