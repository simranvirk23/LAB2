package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3307/db_lab2";
        String username = "root";
        String password = ""; 

        return DriverManager.getConnection(url, username, password);
    }
}