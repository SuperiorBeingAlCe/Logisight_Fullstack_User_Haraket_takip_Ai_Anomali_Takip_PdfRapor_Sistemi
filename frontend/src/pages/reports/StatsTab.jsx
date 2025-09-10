import React, { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import { getSummaryStats, getLoginsByPeriod } from "../../api/Stats";
import { Button, ButtonGroup } from "@mui/material";
import "./StatsTab.css";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

export default function StatsTab() {
  const [summary, setSummary] = useState(null);
  const [logins, setLogins] = useState([]);
  const [period, setPeriod] = useState("day");
  const [uniqueUsers, setUniqueUsers] = useState(0);

  const parseDate = (dateStr) => dateStr ? new Date(dateStr.replace(" ", "T")) : new Date();

  // Summary stats
  useEffect(() => {
    getSummaryStats().then(setSummary);
  }, []);

  // Logins by period
  useEffect(() => {
    const now = new Date();
    let start = new Date();
    if (period === "day") start.setDate(now.getDate() - 7);
    else if (period === "week") start.setMonth(now.getMonth() - 3);
    else if (period === "month") start.setFullYear(now.getFullYear() - 1);

    const startIso = start.toISOString();
    const endIso = now.toISOString();

    getLoginsByPeriod(period, startIso, endIso).then((res) => {
      if (!res) return setLogins([]);

      // Backend artık uniqueUsers ile döndürüyor
      const formatted = res.map((item) => ({
        day: parseDate(item.period).toLocaleDateString(),
        count: item.count,
        uniqueUsers: item.uniqueUsers || 0,
      }));

      // Seçilen periyodun toplam unique user sayısı
      const totalUniqueUsers = formatted.reduce((acc, cur) => acc + cur.uniqueUsers, 0);

      setUniqueUsers(totalUniqueUsers);
      setLogins(formatted);
    });
  }, [period]);

  if (!summary) return <p>Loading...</p>;

  const loginRate = summary.totalUsers > 0
    ? ((uniqueUsers / summary.totalUsers) * 100).toFixed(1)
    : 0;

  const chartData = {
    labels: logins.map((item) => item.day),
    datasets: [
      {
        label: "Logins",
        data: logins.map((item) => item.count),
        borderColor: "rgba(75,192,192,1)",
        backgroundColor: "rgba(75,192,192,0.2)",
        fill: true,
        tension: 0.3,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: { position: "top" },
      title: { display: true, text: "User Logins Over Time" },
    },
  };

  return (
    <div className="stats-tab">
      <h2>📊 Stats Overview</h2>

      <div className="summary-cards">
        <div className="card">
          <h3>Total Users</h3>
          <p>{summary.totalUsers}</p>
        </div>
        <div className="card">
          <h3>Active Users</h3>
          <p>{summary.activeUsers}</p>
        </div>
        <div className="card">
          <h3>Total Reports</h3>
          <p>{summary.totalReports}</p>
        </div>
        <div className="card">
          <h3>Login Rate</h3>
          <p>{loginRate}%</p>
        </div>
      </div>

      <div className="chart-container">
        <Line data={chartData} options={chartOptions} />
      </div>

      <ButtonGroup variant="contained" className="period-buttons">
        <Button onClick={() => setPeriod("day")}>Günlük</Button>
        <Button onClick={() => setPeriod("week")}>Haftalık</Button>
        <Button onClick={() => setPeriod("month")}>Aylık</Button>
      </ButtonGroup>
    </div>
  );
}
