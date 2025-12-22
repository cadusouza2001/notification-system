const { defineConfig } = require("cypress");

module.exports = defineConfig({
  component: {
    devServer: {
      framework: "react",
      bundler: "vite",
    },
    setupNodeEvents(on, config) {
      const codeCoverageTask = require("@cypress/code-coverage/task");
      codeCoverageTask(on, config);
      return config;
    },
  },
  e2e: {
    baseUrl: "http://127.0.0.1:5173",
    setupNodeEvents(on, config) {
      const codeCoverageTask = require("@cypress/code-coverage/task");
      codeCoverageTask(on, config);
      return config;
    },
  },
});
