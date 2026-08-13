# 📚 University Library Management System

A full-stack university library management system built with **Spring Boot**, **React**, and **MySQL**.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-18-149eca?logo=react)
![Vite](https://img.shields.io/badge/Vite-5-646cff?logo=vite)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-red?logo=apachemaven)

## Overview

This project provides separate workflows for library users and administrators.

Users can register, log in, browse and search books, submit reservation requests, cancel pending reservations, and view their reservations and active loans.

Administrators can manage books, authors, and categories, review reservations, register loans, confirm returns, and view library statistics.

The backend exposes a REST API and follows a layered architecture. The frontend is a React single-page application that communicates with the backend through Axios and JSON.

## Features

### User Features

- Registration, login, and logout
- Session-based authentication
- Browse, search, and filter books
- View book details and availability
- Submit and cancel reservations
- View reservation status
- View borrowed books and due dates
- View profile information

### Administrator Features

- Dashboard statistics
- Full CRUD operations for books
- Full CRUD operations for authors
- Full CRUD operations for categories
- View registered users
- Approve or reject reservations
- Automatically create a loan after approving a reservation
- Register loans manually
- Confirm returned books
- Manage book inventory

## Tech Stack

### Backend

- Java 17
- Spring Boot 3.3
- Spring MVC REST
- Spring Data JPA
- Hibernate
- Jakarta Validation
- BCrypt password hashing
- Maven

### Frontend

- React 18
- React Router 6
- Axios
- Vite 5
- Bootstrap 5 RTL
- Bootstrap Icons

### Database and Testing

- MySQL 8
- H2 test database
- Spring Boot Test

## Architecture

```text
React Components
       |
       | Axios / REST / JSON
       v
Spring Boot Controllers
       |
       v
Services
       |
       v
Spring Data JPA Repositories
       |
       v
MySQL Database
```

Authentication is session-based. After a successful login, the server stores the user's ID and role in `HttpSession`. The browser sends the `JSESSIONID` cookie with later API requests.

## Main Entities

- `User`
- `Book`
- `Author`
- `Category`
- `Reservation`
- `Borrow`

Each book belongs to an author and a category. Reservations and loans connect a user to a book.

## Reservation and Borrowing Flow

```text
User submits a reservation
        |
        v
Reservation is created with PENDING status
        |
        v
Administrator approves the reservation
        |
        v
A Borrow record is created automatically
        |
        v
Book quantity is decreased by one
        |
        v
Administrator confirms the return
        |
        v
Borrow is closed and book quantity is restored
```

## Getting Started

### Prerequisites

Install the following tools before running the project:

- Java 17
- Maven 3.9 or later
- Node.js 18 or later
- MySQL 8
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/hamidshaikhy/university-library-management-system-project.git
cd university-library-management-system-project
```

### 2. Create the Database

Run the included `database.sql` file in MySQL Workbench, or execute:

```sql
CREATE DATABASE university_library
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Hibernate creates and updates the project tables automatically when the backend starts.

### 3. Configure MySQL Credentials

Set your MySQL username and password as environment variables before starting the backend.

#### Windows PowerShell

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

#### Linux / macOS

```bash
export DB_USERNAME=root
export DB_PASSWORD=YOUR_MYSQL_PASSWORD
```

Run the backend from the same terminal in which these variables were set.

### 4. Run the Backend

From the project root directory:

```bash
mvn spring-boot:run
```

The backend and REST API will run at:

```text
http://localhost:8081
```

### 5. Run the React Frontend

Open a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the application at:

```text
http://localhost:5173
```

During development, Vite proxies all `/api` requests to `http://localhost:8081`.

## Default Accounts

Sample accounts are created automatically if they do not already exist.

| Role | Email | Password |
|---|---|---|
| Administrator | `admin@library.local` | `Admin123` |
| User | `user@library.local` | `User123` |

These accounts are intended only for local testing and project demonstration.

## Project Structure

```text
university-library-management-system-project/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── lib/
│   │   ├── pages/
│   │   ├── styles/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
├── src/
│   ├── main/java/com/example/library/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── main/resources/
│   │   └── application.properties
│   └── test/
├── database.sql
├── pom.xml
├── .gitignore
└── README.md
```

## Testing

Run the backend tests from the project root:

```bash
mvn test
```

The API integration tests use an H2 database and do not modify the main MySQL database.

To verify the frontend production build:

```bash
cd frontend
npm install
npm run build
```

## Production Build

Build the React frontend first, then package the Spring Boot application:

```bash
cd frontend
npm install
npm run build
cd ..
mvn clean package
```

Run the generated application:

```bash
java -jar target/university-library-1.0.0.jar
```

The React application and REST API will both be available at:

```text
http://localhost:8081
```

## Notes

- Do not commit real database passwords or private credentials.
- Keep `target/`, `frontend/dist/`, and `frontend/node_modules/` out of Git.
- The included accounts and initial records are sample data for local use.
