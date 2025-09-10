import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { isAuthenticated } from "../api/Auth";

export default function PrivateRoute({ children }) {
  const location = useLocation();
  if (isAuthenticated()) {
    return children;
  } else {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
}