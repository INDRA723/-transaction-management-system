-- Run this in MySQL Workbench before starting the project

CREATE DATABASE IF NOT EXISTS transaction_db;
USE transaction_db;

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    holder_name VARCHAR(100) NOT NULL,
    balance DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER_IN', 'TRANSFER_OUT') NOT NULL,
    amount DOUBLE NOT NULL,
    description VARCHAR(255),
    related_account VARCHAR(20),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Sample accounts
INSERT INTO accounts (account_number, holder_name, balance) VALUES
('ACC001', 'Indra Chaganti', 15000.00),
('ACC002', 'Ravi Kumar', 8500.00),
('ACC003', 'Priya Singh', 22000.00);
