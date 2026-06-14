package com.tms.dao;

import com.tms.db.DBConnection;
import com.tms.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean addTransaction(int accountId, String type, double amount,
                                   String description, String relatedAccount) {
        String sql = "INSERT INTO transactions (account_id, type, amount, description, related_account) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, description);
            ps.setString(5, relatedAccount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Transaction> getTransactionsByAccount(int accountId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalTransactionCount() {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalDeposits() {
        String sql = "SELECT SUM(amount) FROM transactions WHERE type='DEPOSIT'";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalWithdrawals() {
        String sql = "SELECT SUM(amount) FROM transactions WHERE type='WITHDRAW'";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getInt("account_id"),
                rs.getString("type"),
                rs.getDouble("amount"),
                rs.getString("description"),
                rs.getString("related_account"),
                rs.getString("transaction_date")
        );
    }
}
