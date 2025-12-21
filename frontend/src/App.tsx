import React, { useState } from "react";
import {
  Container,
  Stack,
  CssBaseline,
  ThemeProvider,
  Box,
  GlobalStyles,
} from "@mui/material";
import { styled, keyframes } from "@mui/material/styles";
import NotificationForm from "./components/NotificationForm";
import LogHistory from "./components/LogHistory";
import theme from "./theme";

const fadeInUp = keyframes`
  0% { opacity: 0; transform: translate3d(0, 10px, 0); }
  100% { opacity: 1; transform: translate3d(0, 0, 0); }
`;

const MainLayout = styled(Box)(({ theme }) => ({
  height: "100vh",
  overflow: "hidden",
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  justifyContent: "center",
  padding: theme.spacing(4),
}));

const globalStyles = {
  "html, body, #root": { height: "100%" },
  body: {
    margin: 0,
    background: "linear-gradient(135deg, #F5F7FA 0%, #C3CFE2 100%)",
    backgroundAttachment: "fixed",
  },
  "#root": { height: "100%" },
};

export default function App() {
  const [refreshKey, setRefreshKey] = useState<number>(0);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <GlobalStyles styles={globalStyles} />

      <MainLayout>
        <Container
          maxWidth="md"
          sx={{
            height: "100%",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
          }}
        >
          <Stack
            spacing={3}
            sx={{
              maxHeight: "100%",
              animation: `${fadeInUp} 0.5s ease both`,
            }}
          >
            <NotificationForm onSent={() => setRefreshKey((k) => k + 1)} />
            <LogHistory refreshKey={refreshKey} />
          </Stack>
        </Container>
      </MainLayout>
    </ThemeProvider>
  );
}
