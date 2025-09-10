import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs"; 
import { API_BASE_URL } from "../api/index";
import { getToken } from "../api/Auth";

export function useUserStatusWS() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = getToken();
    if (!token) return;

    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        console.log("WebSocket bağlandı");
        client.subscribe("/topic/user-status", (message) => {
          if (message.body) {
            setUser(JSON.parse(message.body));
          }
        });
      },
      onStompError: (frame) => {
        console.error("STOMP Hata: ", frame.headers["message"]);
      },
    });

    client.activate();
    return () => client.deactivate();
  }, []);


  return [user, setUser]; 
}