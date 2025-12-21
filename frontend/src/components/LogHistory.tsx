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
} from "@mui/material";
import { fetchLogs, type LogResponse } from "../api";

type Props = {
  refreshKey?: number; // changes when a new send occurs
};

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
    <Card>
      <CardContent>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          mb={2}
        >
          <Typography variant="h6">Log History</Typography>
          <Button onClick={loadData} variant="outlined" disabled={loading}>
            {loading ? "Refreshing..." : "Refresh"}
          </Button>
        </Stack>
        <Table size="small">
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
              <TableRow key={r.id}>
                <TableCell>{r.timestamp}</TableCell>
                <TableCell>{r.type}</TableCell>
                <TableCell>{r.userName}</TableCell>
                <TableCell>{r.category}</TableCell>
                <TableCell>{r.channel}</TableCell>
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
      </CardContent>
    </Card>
  );
}
