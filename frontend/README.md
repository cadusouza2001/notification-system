# Notification Dashboard (Frontend)

Modern dashboard to submit notifications and view delivery logs. Built with React + TypeScript + MUI with a focus on UX (Light Mode, Glassmorphism, Responsive layout).

## Key Features

- **UX**
  - Light mode, responsive layout, glassmorphism touches
- **Tech**
  - React, TypeScript, Vite, MUI (Material UI), Axios
- **Testing**
  - Cypress: component tests for units, E2E tests for flows
  - Coverage: component + E2E supported; HTML and text-summary reports

## Running Locally

- Prerequisites: Node.js 18+ (recommend 20+)
- Install:
  ```bash
  cd frontend
  npm ci
  ```
- Start:
  ```bash
  npm run dev
  ```
- App: http://localhost:5173

## Testing

- Run tests:
  ```bash
  npm test
  ```
  - Component tests run headless; E2E expects the dev server to be up
- Coverage workflow (local):
  ```bash
  npm run test:cov
  ```
  - Reports:
    - HTML: frontend/coverage/index.html
    - Text summary printed to console

## Code Style

- **Prettier**: CI automatically formats and commits code changes when possible
- Manual formatting:
  ```bash
  npm run format:fix
  ```

## Project Notes

- API access through src/api.ts to the backend
- Component tests mount UI in isolation; E2E verifies typical user flows
- CI starts dev server for E2E tests and uploads Cypress artifacts
