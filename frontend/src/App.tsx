import { useState } from "react";
import { Container, Stack } from "@mui/material";
import NotificationForm from "./components/NotificationForm";
import LogHistory from "./components/LogHistory";

export default function App() {
  const [refreshKey, setRefreshKey] = useState<number>(0);

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Stack spacing={3}>
        <NotificationForm onSent={() => setRefreshKey((k) => k + 1)} />
        <LogHistory refreshKey={refreshKey} />
      </Stack>
    </Container>
  );
}
