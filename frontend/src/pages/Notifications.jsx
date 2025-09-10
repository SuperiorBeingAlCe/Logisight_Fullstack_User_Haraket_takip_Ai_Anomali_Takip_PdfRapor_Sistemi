import { createContext, useContext, useEffect, useState, useCallback } from "react";
import * as Notification from "../api/Notification";
import { connectWs, disconnectWs } from "../topic/connectWs";

const NotificationContext = createContext();
export const useNotifications = () => useContext(NotificationContext);

export const NotificationProvider = ({ userId, children }) => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchNotifications = useCallback(async () => {
    setLoading(true);
    try {
      const data = await Notification.getNotificationsByRecipientId(userId);
      setNotifications(data);
    } catch (err) {
      console.error("Bildirimler yüklenemedi", err);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    if (!userId) return;
    fetchNotifications();
    const handleNewNotification = (notif) => setNotifications((prev) => [notif, ...prev]);
    connectWs(null, null, null, handleNewNotification, userId);
    return () => disconnectWs();
  }, [userId, fetchNotifications]);

  const markAsRead = async (id) => {
    try {
      await Notification.markNotificationAsRead(id);
      setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
    } catch (err) {
      console.error(err);
    }
  };

  const deleteNotification = async (id) => {
    try {
      await Notification.deleteNotification(id);
      setNotifications((prev) => prev.filter((n) => n.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <NotificationContext.Provider
      value={{ notifications, loading, fetchNotifications, markAsRead, deleteNotification, unreadCount }}
    >
      {children}
    </NotificationContext.Provider>
  );
};
