import React from "react";
import Sidebar from "./Sidebar";
import Header from "./Header";

export default function MainLayout({ children }) {
  return (
   <div style={{ display: "flex", minHeight: "100vh" }}>
  <Sidebar />  {/* sabit genişlik */}
  <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
    <Header />
    <main style={{ padding: "20px", flex: 1, background: "#f4f4f4" }}>
      {children}
    </main>
  </div>
</div>
  );
}