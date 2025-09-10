import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs"; // tarayıcı uyumlu
import { API_BASE_URL } from "../api/index";
import { getToken } from "../api/Auth";

let stompClient = null;

/**
 * connectWs(onUserUpdate, onUserDelete, onUserList, onNotification, userId)
 * - onUserUpdate(updatedUser) -> kullanıcı eklendi/güncellendi
 * - onUserDelete(deletedId)   -> kullanıcı silme
 * - onUserList(usersArray)    -> backend tam liste gönderirse
 * - onNotification(notif)     -> yeni bildirim
 */
export function connectWs(onUserUpdate, onUserDelete, onUserList, onNotification, userId) {
  const token = getToken();
  if (!token) {
    console.warn("WebSocket: token yok, bağlantı yapılmadı.");
    return;
  }

  if (stompClient && stompClient.active) {
    try {
      stompClient.deactivate();
    } catch (e) {
      console.warn("Önceki WS kapatılırken hata:", e);
    }
    stompClient = null;
  }

  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
    reconnectDelay: 5000,
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    debug: () => {}, 
    onConnect: (frame) => {
      console.log("WebSocket bağlandı:", frame?.headers);

      // --- User Status ---
      try {
        stompClient.subscribe("/topic/user-status", (message) => {
          if (message?.body) {
            try {
              const updatedUser = JSON.parse(message.body);
              if (onUserUpdate) onUserUpdate(updatedUser);
            } catch (e) {
              console.error("user-status parse hatası:", e);
            }
          }
        });
      } catch (e) {
        console.warn("user-status subscribe hatası:", e);
      }

      // --- User Delete ---
      try {
        stompClient.subscribe("/topic/user-delete", (message) => {
          if (message?.body) {
            try {
              const deletedId = JSON.parse(message.body);
              if (onUserDelete) onUserDelete(deletedId);
            } catch (e) {
              console.error("user-delete parse hatası:", e);
            }
          }
        });
      } catch (e) {
        console.warn("user-delete subscribe hatası:", e);
      }

      // --- User List ---
      try {
        stompClient.subscribe("/topic/user-list", (message) => {
          if (message?.body) {
            try {
              const users = JSON.parse(message.body);
              if (onUserList) onUserList(Array.isArray(users) ? users : []);
            } catch (e) {
              console.error("user-list parse hatası:", e);
            }
          }
        });
      } catch {
        // backend bu topic’i açmadıysa sorun değil
      }

      // --- Notifications ---
      if (userId && onNotification) {
        try {
          stompClient.subscribe(`/topic/notifications/${userId}`, (message) => {
            if (message?.body) {
              try {
                const notif = JSON.parse(message.body);
                onNotification(notif);
              } catch (e) {
                console.error("notification parse hatası:", e);
              }
            }
          });
          console.log("Notification topic'e abone olundu:", `/topic/notifications/${userId}`);
        } catch (e) {
          console.warn("notification subscribe hatası:", e);
        }
      }
    },
    onStompError: (frame) => {
      console.error("STOMP Hata:", frame?.headers?.message || frame);
    },
    onWebSocketClose: (evt) => {
      console.log("WebSocket bağlantısı kapandı:", evt);
    },
  });

  stompClient.activate();
}

export function disconnectWs() {
  if (stompClient) {
    try {
      stompClient.deactivate();
      console.log("WebSocket bağlantısı kapatıldı");
    } catch (e) {
      console.warn("disconnectWs hata:", e);
    } finally {
      stompClient = null;
    }
  }
}