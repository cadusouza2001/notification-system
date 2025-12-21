import React, { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  Typography,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Button,
  Stack,
  Chip,
  Box,
  TableContainer,
  CircularProgress,
} from "@mui/material";
import { styled } from "@mui/material/styles";
import RefreshIcon from "@mui/icons-material/Refresh";
import { fetchLogs, type LogResponse } from "../api";
import type { ChipProps } from "@mui/material/Chip";

type Props = {
  refreshKey?: number;
};

const GlassCard = styled(Card)(() => ({
  background: "rgba(255, 255, 255, 0.8)",
  backdropFilter: "blur(12px)",
  border: "1px solid rgba(255, 255, 255, 0.3)",
  boxShadow:
    "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)",
  flex: 1,
  display: "flex",
  flexDirection: "column",
  overflow: "hidden",
}));

const RefreshButton = styled(Button)(() => ({
  minWidth: 140,
  transition: "transform 0.2s ease",
  "&:hover": {
    transform: "scale(1.02)",
  },
}));

// Helper Functions
function getCategoryColor(name: string): ChipProps["color"] {
  switch (name) {
    case "Sports":
      return "secondary";
    case "Finance":
      return "primary";
    case "Movies":
      return "default";
    default:
      return "default";
  }
}

function getChannelColor(name: string): ChipProps["color"] {
  switch (name) {
    case "SMS":
      return "default";
    case "E-Mail":
      return "primary";
    case "Push Notification":
      return "secondary";
    default:
      return "default";
  }
}

function formatTimestamp(ts: string): string {
  try {
    const d = new Date(ts);
    return d.toLocaleString("en-US", {
      month: "short",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    });
  } catch {
    return ts;
  }
}

export default function LogHistory({ refreshKey }: Props) {
  const [rows, setRows] = useState<LogResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await fetchLogs();
      setRows(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [refreshKey]);

  return (
    <GlassCard>
      <CardContent
        sx={{
          display: "flex",
          flexDirection: "column",
          height: "100%",
          gap: 2,
        }}
      >
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
        >
          <Typography variant="h6">Log History</Typography>
          <RefreshButton
            onClick={loadData}
            variant="outlined"
            disabled={loading}
            startIcon={
              loading ? <CircularProgress size={18} /> : <RefreshIcon />
            }
          >
            Refresh
          </RefreshButton>
        </Stack>

        <TableContainer
          component={Box}
          sx={{
            flex: 1,
            minHeight: 0,
            overflowY: "auto",
            overflowX: "auto",
            "&::-webkit-scrollbar": { width: "8px" },
          }}
        >
          <Table size="small" stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell>Time</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>User Name</TableCell>
                <TableCell>Category</TableCell>
                <TableCell>Channel</TableCell>
                <TableCell>Message</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((r) => (
                <TableRow key={r.id} hover>
                  <TableCell>{formatTimestamp(r.timestamp)}</TableCell>
                  <TableCell>{r.type}</TableCell>
                  <TableCell>{r.userName}</TableCell>
                  <TableCell>
                    <Chip
                      label={r.category}
                      color={getCategoryColor(r.category)}
                    />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={r.channel}
                      color={getChannelColor(r.channel)}
                    />
                  </TableCell>
                  <TableCell>{r.message}</TableCell>
                </TableRow>
              ))}
              {rows.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} align="center">
                    No logs found.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>
    </GlassCard>
  );
}
