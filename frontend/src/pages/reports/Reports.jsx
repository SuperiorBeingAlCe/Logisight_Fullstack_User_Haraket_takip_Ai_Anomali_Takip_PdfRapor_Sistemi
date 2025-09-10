import React, { useState } from "react";
import { Tabs, Tab, Box } from "@mui/material";
import StatsTab from "./StatsTab";
import ReportsTab from "./ReportsTab";

export default function Reports() {
  const [tab, setTab] = useState(0);

  const handleChange = (event, newValue) => {
    setTab(newValue);
  };

  return (
    <Box sx={{ width: "100%" }}>
      {/* Sekme başlıkları */}
      <Tabs value={tab} onChange={handleChange} aria-label="reports-tabs">
        <Tab label="📊 Stats" />
        <Tab label="📄 Reports" />
      </Tabs>

      {/* Sekme içerikleri */}
      <Box sx={{ mt: 2 }}>
        {tab === 0 && <StatsTab />}
        {tab === 1 && <ReportsTab />}
      </Box>
    </Box>
  );
}