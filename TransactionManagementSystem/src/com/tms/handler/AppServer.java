package com.tms.handler;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.tms.dao.AccountDAO;
import com.tms.dao.TransactionDAO;
import com.tms.db.DBConnection;
import com.tms.model.Account;
import com.tms.model.Transaction;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class AppServer {

    static AccountDAO accountDAO = new AccountDAO();
    static TransactionDAO transactionDAO = new TransactionDAO();

    public static void main(String[] args) throws Exception {
        DBConnection.getConnection();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // API routes
        server.createContext("/api/accounts", AppServer::handleAccounts);
        server.createContext("/api/transactions", AppServer::handleTransactions);
        server.createContext("/api/deposit", AppServer::handleDeposit);
        server.createContext("/api/withdraw", AppServer::handleWithdraw);
        server.createContext("/api/transfer", AppServer::handleTransfer);
        server.createContext("/api/summary", AppServer::handleSummary);
        server.createContext("/api/create-account", AppServer::handleCreateAccount);

        // Serve frontend files
        server.createContext("/", AppServer::handleFrontend);

        server.start();
        System.out.println("✔ Server running at http://localhost:8080");
        System.out.println("  Open your browser and go to http://localhost:8080");
    }

    // ─── SERVE FRONTEND ──────────────────────────────────────────────────────
    static void handleFrontend(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/") || path.equals("/index.html")) {
            File f = new File("frontend/index.html");
            sendFile(ex, f, "text/html");
        } else {
            sendResponse(ex, 404, "Not Found");
        }
    }

    // ─── GET ALL ACCOUNTS ────────────────────────────────────────────────────
    static void handleAccounts(HttpExchange ex) throws IOException {
        addCors(ex);
        if (ex.getRequestMethod().equals("OPTIONS")) { ex.sendResponseHeaders(204, -1); return; }

        List<Account> accounts = accountDAO.getAllAccounts();
        String json = "[" + accounts.stream().map(Account::toJson).collect(Collectors.joining(",")) + "]";
        sendJson(ex, 200, json);
    }

    // ─── GET ALL TRANSACTIONS ─────────────────────────────────────────────────
    static void handleTransactions(HttpExchange ex) throws IOException {
        addCors(ex);
        if (ex.getRequestMethod().equals("OPTIONS")) { ex.sendResponseHeaders(204, -1); return; }

        List<Transaction> txns = transactionDAO.getAllTransactions();
        String json = "[" + txns.stream().map(Transaction::toJson).collect(Collectors.joining(",")) + "]";
        sendJson(ex, 200, json);
    }

    // ─── DEPOSIT ─────────────────────────────────────────────────────────────
    static void handleDeposit(HttpExchange ex) throws IOException {
        addCors(ex);
        if (ex.getRequestMethod().equals("OPTIONS")) { ex.sendResponseHeaders(204, -1); return; }

        String body = readBody(ex);
        String accNum = extractJson(body, "accountNumber");
        double amount = Double.parseDouble(extractJson(body, "amount"));
        String desc = extractJson(body, "description");

        Account acc = accountDAO.getAccountByNumber(accNum);
        if (acc == null) { sendJson(ex, 404, "{\"error\":\"Account not found\"}"); return; }
        if (amount <= 0) { sendJson(ex, 400, "{\"error\":\"Amount must be greater than 0\"}"); return; }

        double newBalance = acc.getBalance() + amount;
        accountDAO.updateBalance(accNum, newBalance);
        transactionDAO.addTransaction(acc.getId(), "DEPOSIT", amount, desc, null);

        sendJson(ex, 200, "{\"message\":\"Deposit successful\",\"newBalance\":" + newBalance + "}");
    }

    // ─── WITHDRAW ────────────────────────────────────────────────────────────
    static void handleWithdraw(HttpExchange ex) throws IOException {
        addCors(ex);
        if (ex.getRequestMethod().equals("OPTIONS")) { ex.sendResponseHeaders(204, -1); return; }

        String body = readBody(ex);
        String accNum = extractJson(body, "accountNumber");
        double amount = Double.parseDouble(extractJson(body, "amount"));
        String desc = extractJson(body, "description");

        Account acc = accountDAO.getAccountByNumber(accNum);
        if (acc == null) { sendJson(ex, 404, "{\"error\":\"Account not found\"}"); return; }
        if (amount <= 0) { sendJson(ex, 400, "{\"error\":\"Amount must be greater than 0\"}"); return; }
        if (acc.getBalance() < amount) { sendJson(ex, 400, "{\"error\":\"Insufficient balance\"}"); return; }

        double newBalance = acc.getBalance() - amount;
        accountDAO.updateBalance(accNum, newBalance);
        transactionDAO.addTransaction(acc.getId(), "WITHDRAW", amount, desc, null);

        sendJson(ex, 200, "{\"message\":\"Withdrawal successful\",\"newBalance\":" + newBalance + "}");
    }

    // ─── TRANSFER ────────────────────────────────────────────────────────────
    static void handleTransfer(HttpExchange ex) throws IOException {
        addCors(ex);
        if (ex.getRequestMethod().equals("OPTIONS")) { ex.sendResponseHeaders(204, -1); return; }

        String body = readBody(ex);
        String fromAcc = extractJson(body, "fromAccount");
        String toAcc = extractJson(body, "toAccount");
        double amount = Double.parseDouble(extractJson(body, "amount"));
        String desc = extractJson(body, "description");

        Account from = accountDAO.getAccountByNumber(fromAcc);
        Account to = accountDAO.getAccountByNumber(toAcc);

        if (from == null || to == null) { sendJson(ex, 404, "{\"error\":\"One or both accounts not found\"}"); return; }
        if (from.getBalance() < amount) { sendJson(ex, 400, "{\"error\":\"Insufficient balance\"}"); return; }
        if (fromAcc.equals(toAcc)) { sendJson(ex, 400, "{\"error\":\"Cannot transfer to same account\"}"); return; }

        accountDAO.updateBalance(fromAcc, from.getBalance() - amount);
        accountDAO.updateBalance(toAcc, to.getBalance() + amount);
        transactionDAO.addTransaction(from.getId(), "TRANSFER_OUT", amount, desc, toAcc);
        transactionDAO.addTransaction(to.getId(), "TRANSFER_IN", amount, desc, fromAcc);

        sendJson(ex, 200, "{\"message\":\"Transfer successful\"}");
    }

    // ─── SUMMARY ─────────────────────────────────────────────────────────────
    static void handleSummary(HttpExchange ex) throws IOException {
        addCors(ex);
        if (ex.getRequestMethod().equals("OPTIONS")) { ex.sendResponseHeaders(204, -1); return; }

        double totalBalance = accountDAO.getTotalBalance();
        int totalTxns = transactionDAO.getTotalTransactionCount();
        double totalDeposits = transactionDAO.getTotalDeposits();
        double totalWithdrawals = transactionDAO.getTotalWithdrawals();
        int totalAccounts = accountDAO.getAllAccounts().size();

        String json = String.format(
                "{\"totalBalance\":%.2f,\"totalTransactions\":%d,\"totalDeposits\":%.2f,\"totalWithdrawals\":%.2f,\"totalAccounts\":%d}",
                totalBalance, totalTxns, totalDeposits, totalWithdrawals, totalAccounts);
        sendJson(ex, 200, json);
    }

    // ─── CREATE ACCOUNT ───────────────────────────────────────────────────────
    static void handleCreateAccount(HttpExchange ex) throws IOException {
        addCors(ex);
        if (ex.getRequestMethod().equals("OPTIONS")) { ex.sendResponseHeaders(204, -1); return; }

        String body = readBody(ex);
        String accNum = extractJson(body, "accountNumber");
        String holderName = extractJson(body, "holderName");
        double initialBalance = Double.parseDouble(extractJson(body, "initialBalance"));

        boolean success = accountDAO.createAccount(accNum, holderName, initialBalance);
        if (success) sendJson(ex, 200, "{\"message\":\"Account created successfully\"}");
        else sendJson(ex, 500, "{\"error\":\"Failed to create account\"}");
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    static void sendJson(HttpExchange ex, int code, String json) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    static void sendResponse(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    static void sendFile(HttpExchange ex, File f, String contentType) throws IOException {
        if (!f.exists()) { sendResponse(ex, 404, "File not found"); return; }
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(200, f.length());
        try (FileInputStream fis = new FileInputStream(f)) {
            fis.transferTo(ex.getResponseBody());
        }
        ex.getResponseBody().close();
    }

    static void addCors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    static String extractJson(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        } else {
            int end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('}', start);
            return json.substring(start, end).trim();
        }
    }
}
