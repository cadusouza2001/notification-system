import React, { useState } from "react";
import {
  Container,
  Stack,
  CssBaseline,
  ThemeProvider,
  Box,
  GlobalStyles,
} from "@mui/material";
import NotificationForm from "./components/NotificationForm";
import LogHistory from "./components/LogHistory";
import theme from "./theme";

export default function App() {
  const [refreshKey, setRefreshKey] = useState<number>(0);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <GlobalStyles
        styles={{
          "html, body, #root": { height: "100%" },
          body: {
            margin: 0,
            background: "linear-gradient(135deg, #F5F7FA 0%, #C3CFE2 100%)",
            backgroundAttachment: "fixed",
          },
          "#root": { height: "100%" },
        }}
      />
      <style>
        {`
          @keyframes fadeInUp {
            0% {
              opacity: 0;
              transform: translate3d(0, 10px, 0);
            }
            100% {
              opacity: 1;
              transform: translate3d(0, 0, 0);
            }
          }
        `}
      </style>

      <Box
        sx={{
          height: "100vh",
          overflow: "hidden",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          p: 4,
        }}
      >
        <Container
          maxWidth="md"
          sx={{
            height: "100%",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
          }}
        >
          <Stack spacing={3} sx={{ maxHeight: "100%" }}>
            <NotificationForm onSent={() => setRefreshKey((k) => k + 1)} />
            <LogHistory refreshKey={refreshKey} />
          </Stack>
        </Container>
      </Box>
    </ThemeProvider>
  );
}
