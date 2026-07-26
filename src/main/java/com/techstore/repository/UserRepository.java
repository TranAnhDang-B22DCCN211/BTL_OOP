package com.techstore.repository;

import com.techstore.exception.UserException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public String kiemTraDangNhap(String username, String password) {
        String sql = "SELECT vai_tro FROM user WHERE ten_dang_nhap=? AND mat_khau=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("vai_tro");
                }
            }
        } catch (SQLException e) {
            throw new UserException("Không thể kiểm tra thông tin đăng nhập trong MySQL!", e);
        }
        return null;
    }
}
