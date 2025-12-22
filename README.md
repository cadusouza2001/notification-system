# Enterprise Notification System

[![Build Status](https://github.com/cadusouza2001/notification-system/actions/workflows/ci.yml/badge.svg)](https://github.com/cadusouza2001/notification-system/actions/workflows/ci.yml)
![Coverage](.github/badges/jacoco.svg)
![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot&logoColor=white)
![React 19](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)

A production-ready, full-stack notification platform designed with **Clean Architecture** and **SOLID principles**. This repository demonstrates a resilient microservice capable of routing notifications across multiple strategies (SMS, Email, Push) with a modern React dashboard.

## Quick Start

1. Ensure Docker is running.
2. Run the stack:
   ```bash
   docker-compose up --build
   ```
3. Access the services:
   - **Frontend Dashboard**: http://localhost:5173
   - **Backend API**: http://localhost:8080
   - **PostgreSQL**: http://localhost:5432

The command above will start all three services: PostgreSQL database, Spring Boot backend, and React frontend.

## Architecture Highlights

The system allows for pluggable notification channels without modifying core logic (Strategy Pattern).

### Backend (Spring Boot 3)

- **Hexagonal/Clean Architecture**: Strict separation between Domain (Core), Application (Use Cases), and Infrastructure (Adapters).
- **Resilience**: Failures in one channel do not crash the transaction.
- **Testing**: 85%+ Test Coverage using JUnit 5, Mockito, and H2 Integration Tests.
- **Style**: Google Java Format enforced via Spotless.

### Frontend (React + MUI)

- **Modern UX**: Glassmorphism design system.
- **Robustness**: Full E2E testing flows with Cypress.
- **Style**: Prettier enforced.

## Repository Structure

- `backend/` - Java Source, Tests, Flyway Migrations.
- `frontend/` - React Source, Cypress Tests.
- `docker-compose.yml` - Container orchestration for App + PostgreSQL.

## Continuous Integration

The GitHub Actions pipeline (`.github/workflows/ci.yml`) provides automated quality assurance:

- **Auto-formatting**: Automatically formats code using Spotless (backend) and Prettier (frontend), then commits changes.
- **Tests**: Runs Maven (Backend) and Cypress (Frontend) test suites.
- **Coverage**: Automatically generates and updates the coverage badge from JaCoCo reports.

Note: Auto-commit requires write permissions; PRs from forks may not auto-commit due to GitHub security restrictions.

## Documentation

- **Backend**: [./backend/README.md](./backend/README.md)
- **Frontend**: [./frontend/README.md](./frontend/README.md)
