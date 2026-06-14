package com.tms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/transaction_db";
    private static final String USERNAME = "root";         // your MySQL username
    private static final String PASSWORD = "enter your password"; // your MySQL password

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✔ Database connected!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("✘ MySQL Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("✘ Connection failed! Check credentials.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
