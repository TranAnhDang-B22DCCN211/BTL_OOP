package com.techstore.repository;

import com.techstore.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/techstore_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Không tìm thấy MySQL Driver!", e);
        } catch (SQLException e) {
            throw new DatabaseException("Không thể kết nối tới MySQL!", e);
        }
    }
}
