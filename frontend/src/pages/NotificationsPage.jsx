import React from "react";
import { useUser } from "../context/UserContext";
import { NotificationProvider } from "./Notification";
import { NotificationList } from "./NotificationList";

export default function NotificationsPage() {
  const { user } = useUser();
  if (!user) return <div>Yükleniyor...</div>;

  return (
    <NotificationProvider userId={user.id}>
      <NotificationList />
    </NotificationProvider>
  );
}