package com.tms.dao;

import com.tms.db.DBConnection;
import com.tms.model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Account(rs.getInt("id"), rs.getString("account_number"),
                        rs.getString("holder_name"), rs.getDouble("balance"),
                        rs.getString("created_at")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Account getAccountByNumber(String accNum) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, accNum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("id"), rs.getString("account_number"),
                        rs.getString("holder_name"), rs.getDouble("balance"),
                        rs.getString("created_at"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean createAccount(String accountNumber, String holderName, double initialBalance) {
        String sql = "INSERT INTO accounts (account_number, holder_name, balance) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setString(2, holderName);
            ps.setDouble(3, initialBalance);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public double getTotalBalance() {
        String sql = "SELECT SUM(balance) FROM accounts";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
