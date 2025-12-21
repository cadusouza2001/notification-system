import React, { useState } from "react";
import {
  Card,
  CardContent,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Button,
  Snackbar,
  Alert,
  Stack,
  CircularProgress,
} from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import { sendNotification } from "../api";

type Props = {
  onSent?: () => void;
};

const categories = ["Sports", "Finance", "Movies"];
const glassSx = {
  background: "rgba(255, 255, 255, 0.8)",
  backdropFilter: "blur(12px)",
  border: "1px solid rgba(255, 255, 255, 0.3)",
  boxShadow:
    "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)",
};

export default function NotificationForm({ onSent }: Props) {
  const [category, setCategory] = useState<string>(categories[0]);
  const [message, setMessage] = useState<string>("");
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [toastOpen, setToastOpen] = useState<boolean>(false);
  const [toastError, setToastError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!message.trim()) {
      setToastError("Message is required");
      setToastOpen(true);
      return;
    }
    setSubmitting(true);
    try {
      await sendNotification({ category, message });
      setToastError(null);
      setToastOpen(true);
      setMessage("");
      if (onSent) onSent();
    } catch (err: any) {
      setToastError(err?.response?.data ?? "Failed to send notification");
      setToastOpen(true);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Card sx={glassSx}>
      <CardContent>
        <Typography
          variant="h6"
          gutterBottom
          sx={{ animation: "fadeInUp 0.5s ease both" }}
        >
          Send Notification
        </Typography>
        <form onSubmit={handleSubmit}>
          <Stack spacing={2}>
            <FormControl fullWidth>
              <InputLabel id="category-label">Category</InputLabel>
              <Select
                labelId="category-label"
                value={category}
                label="Category"
                onChange={(e) => setCategory(e.target.value as string)}
              >
                {categories.map((c) => (
                  <MenuItem key={c} value={c}>
                    {c}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              label="Message"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              fullWidth
              multiline
              minRows={3}
              required
            />

            <Button
              type="submit"
              variant="contained"
              disabled={submitting}
              sx={{
                transition: "transform 0.2s ease",
                "&:hover": { transform: "scale(1.02)" },
                alignSelf: "flex-start",
              }}
            >
              {submitting ? (
                <>
                  <CircularProgress size={20} sx={{ mr: 1 }} />
                  Sending...
                </>
              ) : (
                <>
                  <SendIcon sx={{ mr: 1 }} />
                  Send
                </>
              )}
            </Button>
          </Stack>
        </form>

        <Snackbar
          open={toastOpen}
          autoHideDuration={4000}
          onClose={() => setToastOpen(false)}
        >
          <Alert
            onClose={() => setToastOpen(false)}
            severity={toastError ? "error" : "success"}
            sx={{ width: "100%" }}
          >
            {toastError ? toastError : "Notification sent successfully"}
          </Alert>
        </Snackbar>
      </CardContent>
    </Card>
  );
}
