# Transaction Management System
Bank-style transaction system built with Java + JDBC + MySQL + HTML/CSS/JS

## Tech Stack
- Java 17+ (built-in HttpServer — no Spring Boot needed)
- JDBC + MySQL
- HTML5, CSS3, Vanilla JavaScript

## Project Structure
```
TransactionManagementSystem/
├── database_setup.sql
├── frontend/
│   └── index.html          → Full dashboard UI
└── src/com/tms/
    ├── db/DBConnection.java       → MySQL connection
    ├── model/Account.java         → Account model
    ├── model/Transaction.java     → Transaction model
    ├── dao/AccountDAO.java        → Account DB operations
    ├── dao/TransactionDAO.java    → Transaction DB operations
    └── handler/AppServer.java     → HTTP Server + all API routes
```

## Features
- Dashboard with summary cards (total balance, deposits, withdrawals)
- View all accounts as bank-style cards
- Full transaction history table
- Deposit money into any account
- Withdraw money (with balance check)
- Transfer between accounts
- Create new accounts

## Setup Steps

### Step 1 — Setup MySQL
Run `database_setup.sql` in MySQL Workbench

### Step 2 — Update DB Credentials
Open `DBConnection.java`:
```java
private static final String USERNAME = "root";
private static final String PASSWORD = "yourpassword";
```

### Step 3 — Add MySQL Connector JAR
IntelliJ → File → Project Structure → Libraries → Add mysql-connector-java jar

### Step 4 — Run
Run `AppServer.java` → open browser → http://localhost:8080

## API Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/accounts | Get all accounts |
| GET | /api/transactions | Get all transactions |
| GET | /api/summary | Get dashboard summary |
| POST | /api/deposit | Deposit money |
| POST | /api/withdraw | Withdraw money |
| POST | /api/transfer | Transfer between accounts |
| POST | /api/create-account | Create new account |
