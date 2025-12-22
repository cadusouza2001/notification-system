import React from "react";
import { mount } from "cypress/react";
import { ThemeProvider, CssBaseline } from "@mui/material";
import theme from "../../src/theme";
import "@cypress/code-coverage/support";

// Augment Cypress types to include the mount command
declare global {
  namespace Cypress {
    interface Chainable {
      mount: typeof mount;
    }
  }
}

Cypress.Commands.add("mount", (component: React.ReactNode, options?: any) => {
  return mount(
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {component}
    </ThemeProvider>,
    options
  );
});
