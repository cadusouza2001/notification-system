import axios from "axios";

export interface NotificationRequest {
  category: string;
  message: string;
}

export interface LogResponse {
  id: number;
  type: string;
  userName: string;
  category: string;
  channel: string;
  message: string;
  timestamp: string;
}

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8082",
});

export const sendNotification = (req: NotificationRequest) =>
  api.post("/api/notifications", req);

export const fetchLogs = () => api.get<LogResponse[]>("/api/notifications/log");

export default api;
