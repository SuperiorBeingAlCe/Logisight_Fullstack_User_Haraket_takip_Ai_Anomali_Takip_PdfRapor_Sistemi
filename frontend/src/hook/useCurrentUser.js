import { useState, useEffect } from "react";
import { getCurrentUser } from "../api/Auth";
import { connectWs, disconnectWs } from "../topic/connectWs";

export function useCurrentUser() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getCurrentUser()
      .then((u) => setUser(u))
      .catch((err) => console.error("Current user fetch error:", err))
      .finally(() => setLoading(false));

    connectWs(setUser);

    return () => disconnectWs();
  }, []);

  return [user, loading, setUser];
}