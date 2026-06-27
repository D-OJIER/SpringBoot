# Water Management System

A full-stack water distribution management application built with Spring Boot and React. The system enables apartment and water source management, daily water usage tracking, billing configuration, and reporting through a secure JWT-based authentication system.

---

## Features

* JWT-based authentication and authorization
* Role-based access control
* Apartment management
* Apartment type management
* Block management
* Water source configuration
* Water rate management
* Apartment source configuration
* Daily water usage logging
* Monthly consumption summaries
* Dashboard statistics and reporting
* Usage analytics and charts

---

## Architecture

* **Architecture Style:** Domain-Driven Design (DDD) with Vertical Slicing
* **Backend:** Spring Boot 3, Spring Security, Spring Data JPA
* **Frontend:** React + Vite
* **Database:** MySQL 8
* **Authentication:** JWT

---

## Technology Stack

| Layer            | Technology                 |
| ---------------- | -------------------------- |
| Backend          | Spring Boot 3              |
| Security         | Spring Security, JWT       |
| ORM              | Hibernate, Spring Data JPA |
| Database         | MySQL 8                    |
| Frontend         | React, Vite                |
| Build Tool       | Maven                      |
| Containerization | Docker, Docker Compose     |

---

## Project Structure

```text
.
├── backend/
│   ├── src/main/java/com/management/water/
│   │   ├── modules/
│   │   └── security/
│   └── src/main/resources/
├── frontend/
│   ├── src/components/
│   ├── src/pages/
│   ├── src/api/
│   └── src/styles/
├── docker-compose.yml
└── README.md
```

---

## Prerequisites

* Java 17 or later
* Node.js 18 or later
* MySQL 8
* Docker and Docker Compose (optional)

---

## Configuration

Create a `.env.local` file in the backend project root.

Example:

```env
DB_URL=jdbc:mysql://localhost:3307/water_management?createDatabaseIfNotExist=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

ADMIN_USERNAME=your_admin_username
ADMIN_PASSWORD=your_admin_password

JWT_SECRET=your_jwt_secret
```

---

## Running with Docker

Start all services:

```bash
docker-compose up
```

### Services

| Service  | URL                   |
| -------- | --------------------- |
| Backend  | http://localhost:8083 |
| Frontend | http://localhost:5173 |

---

## Running Locally

### Backend

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## API Endpoints

### Authentication

```http
POST /auth/login
```

### System

```http
GET /test
```

### Apartments

```http
GET  /apartments
POST /apartments
```

### Apartment Types

```http
GET  /apartment-types
POST /apartment-types
```

### Blocks

```http
GET    /blocks
POST   /blocks
DELETE /blocks/{id}
```

### Water Sources

```http
GET  /water-sources
POST /water-sources
```

### Water Rates

```http
GET    /water-rates
POST   /water-rates
PUT    /water-rates/{id}
DELETE /water-rates/{id}
```

### Apartment Source Configurations

```http
GET  /apartment-source-configs
POST /apartment-source-configs
```

### Daily Logs

```http
GET  /daily-logs
POST /daily-logs
```

### Dashboard

```http
GET /dashboard/stats
```

### Monthly Summary

```http
GET /monthly-summary
```

---

## API Overview

| Domain              | Description                                                 |
| ------------------- | ----------------------------------------------------------- |
| Authentication      | User login and JWT generation                               |
| Property Management | Apartments, Apartment Types, Blocks                         |
| Water Configuration | Water Sources, Water Rates, Apartment Source Configurations |
| Telemetry           | Daily Water Usage Logs                                      |
| Reporting           | Dashboard Statistics and Monthly Summary                    |

---

## Application URLs

### Backend

```text
http://localhost:8083
```

### Frontend

```text
http://localhost:5173
```

---

## Testing

Run backend tests:

```bash
cd backend
./mvnw test
```

---

## Production Build

### Backend

```bash
cd backend
./mvnw clean package
```

### Frontend

```bash
cd frontend
npm run build
```
