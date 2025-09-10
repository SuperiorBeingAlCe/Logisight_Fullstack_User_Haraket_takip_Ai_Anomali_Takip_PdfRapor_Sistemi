import React from "react";
import { useNotifications } from "./Notifications";


export const NotificationList = () => {
  const { notifications, loading, markAsRead, deleteNotification } = useNotifications();

  if (loading) return <p>Yükleniyor...</p>;
  if (notifications.length === 0) return <p>Hiç bildirim yok.</p>;

  return (
    <ul className="notification-list">
      {notifications.map((n) => (
        <li key={n.id} className={n.read ? "read" : "unread"}>
          <p>{n.message}</p>
          <button onClick={() => markAsRead(n.id)}>Okundu</button>
          <button onClick={() => deleteNotification(n.id)}>Sil</button>
        </li>
      ))}
    </ul>
  );
};
