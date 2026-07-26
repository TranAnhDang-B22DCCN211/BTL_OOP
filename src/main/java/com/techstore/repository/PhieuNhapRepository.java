package com.techstore.repository;

import com.techstore.model.ChiTietPhieuNhap;
import com.techstore.model.PhieuNhap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapRepository implements BaseRepository<PhieuNhap> {

    @Override
    public boolean insert(PhieuNhap entity) {
        String sqlPhieuNhap = "INSERT INTO phieu_nhap (ma_phieu_nhap, ngay_nhap, nguoi_nhap) VALUES (?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chi_tiet_phieu_nhap (ma_phieu_nhap, ma_sp, so_luong_nhap) VALUES (?, ?, ?)";
        String sqlCapNhatKho = "UPDATE san_pham SET so_luong = so_luong + ? WHERE ma_sp = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPN = conn.prepareStatement(sqlPhieuNhap)) {
                pstmtPN.setString(1, entity.getMaPhieuNhap());
                pstmtPN.setTimestamp(2, new java.sql.Timestamp(entity.getNgayNhap().getTime()));
                pstmtPN.setString(3, entity.getNguoiNhap());
                pstmtPN.executeUpdate();
            }

            try (PreparedStatement pstmtCT = conn.prepareStatement(sqlChiTiet);
                    PreparedStatement pstmtKho = conn.prepareStatement(sqlCapNhatKho)) {

                for (ChiTietPhieuNhap ct : entity.getDanhSachChiTiet()) {
                    pstmtCT.setString(1, entity.getMaPhieuNhap());
                    pstmtCT.setString(2, ct.getMaSanPham());
                    pstmtCT.setInt(3, ct.getSoLuongNhap());
                    pstmtCT.addBatch();

                    pstmtKho.setInt(1, ct.getSoLuongNhap());
                    pstmtKho.setString(2, ct.getMaSanPham());
                    pstmtKho.addBatch();
                }

                pstmtCT.executeBatch();
                pstmtKho.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean update(PhieuNhap entity) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public List<PhieuNhap> findAll() {
        List<PhieuNhap> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phieu_nhap ORDER BY ngay_nhap DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap();
                pn.setMaPhieuNhap(rs.getString("ma_phieu_nhap"));
                pn.setNgayNhap(rs.getTimestamp("ngay_nhap"));
                pn.setNguoiNhap(rs.getString("nguoi_nhap"));
                danhSach.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    @Override
    public PhieuNhap findById(String id) {
        String sql = "SELECT * FROM phieu_nhap WHERE ma_phieu_nhap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    PhieuNhap pn = new PhieuNhap();
                    pn.setMaPhieuNhap(rs.getString("ma_phieu_nhap"));
                    pn.setNgayNhap(rs.getTimestamp("ngay_nhap"));
                    pn.setNguoiNhap(rs.getString("nguoi_nhap"));
                    return pn;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
