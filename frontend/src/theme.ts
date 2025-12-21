import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    mode: "light",
    primary: { main: "#2997D6" }, // Brand Blue
    secondary: { main: "#F58E26" }, // Brand Orange
    background: {
      default: "#F0F2F5", // Light Grey
      paper: "#FFFFFF", // White
    },
  },
});

export default theme;
