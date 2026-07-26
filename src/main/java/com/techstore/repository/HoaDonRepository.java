package com.techstore.repository;

import com.techstore.exception.DatabaseException;
import com.techstore.model.ChiTietHoaDon;
import com.techstore.model.HoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HoaDonRepository implements BaseRepository<HoaDon> {

    @Override
    public boolean insert(HoaDon entity) {
        String sqlHoaDon = "INSERT INTO hoa_don (ma_hd, ten_kh, ngay_tao) VALUES (?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chi_tiet_hoa_don (ma_hd, ma_sp, so_luong, don_gia) VALUES (?, ?, ?, ?)";
        String sqlCapNhatKho = "UPDATE san_pham SET so_luong = so_luong - ? WHERE ma_sp = ? AND so_luong >= ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtHD = conn.prepareStatement(sqlHoaDon)) {
                pstmtHD.setString(1, entity.getMaHoaDon());
                pstmtHD.setString(2, entity.getTenKhachHang());
                pstmtHD.setTimestamp(3, new java.sql.Timestamp(entity.getNgayTao().getTime()));
                pstmtHD.executeUpdate();
            }

            try (PreparedStatement pstmtCT = conn.prepareStatement(sqlChiTiet);
                    PreparedStatement pstmtKho = conn.prepareStatement(sqlCapNhatKho)) {
                for (ChiTietHoaDon ct : entity.getDanhSachChiTiet()) {
                    pstmtCT.setString(1, entity.getMaHoaDon());
                    pstmtCT.setString(2, ct.getMaSanPham());
                    pstmtCT.setInt(3, ct.getSoLuong());
                    pstmtCT.setDouble(4, ct.getDonGia());
                    pstmtCT.addBatch();

                    pstmtKho.setInt(1, ct.getSoLuong());
                    pstmtKho.setString(2, ct.getMaSanPham());
                    pstmtKho.setInt(3, ct.getSoLuong());
                    int affectedRows = pstmtKho.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Không đủ tồn kho cho sản phẩm: " + ct.getMaSanPham());
                    }
                }
                pstmtCT.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Không thể rollback giao dịch tạo hóa đơn!", ex);
                }
            }
            throw new DatabaseException("Không thể lưu hóa đơn vào MySQL!", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new DatabaseException("Không thể đóng kết nối MySQL sau khi tạo hóa đơn!", e);
                }
            }
        }
    }

    @Override
    public boolean update(HoaDon entity) {
        String sql = "UPDATE hoa_don SET ten_kh=?, ngay_tao=? WHERE ma_hd=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getTenKhachHang());
            pstmt.setTimestamp(2, new java.sql.Timestamp(entity.getNgayTao().getTime()));
            pstmt.setString(3, entity.getMaHoaDon());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Không thể cập nhật hóa đơn trong MySQL!", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sqlChiTiet = "DELETE FROM chi_tiet_hoa_don WHERE ma_hd=?";
        String sqlHoaDon = "DELETE FROM hoa_don WHERE ma_hd=?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtCT = conn.prepareStatement(sqlChiTiet)) {
                pstmtCT.setString(1, id);
                pstmtCT.executeUpdate();
            }

            try (PreparedStatement pstmtHD = conn.prepareStatement(sqlHoaDon)) {
                pstmtHD.setString(1, id);
                pstmtHD.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Không thể rollback giao dịch xóa hóa đơn!", ex);
                }
            }
            throw new DatabaseException("Không thể xóa hóa đơn khỏi MySQL!", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new DatabaseException("Không thể đóng kết nối MySQL sau khi xóa hóa đơn!", e);
                }
            }
        }
    }

    @Override
    public List<HoaDon> findAll() {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don ORDER BY ngay_tao DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(rs.getString("ma_hd"));
                hd.setTenKhachHang(rs.getString("ten_kh"));
                hd.setNgayTao(rs.getTimestamp("ngay_tao"));
                danhSach.add(hd);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Không thể lấy danh sách hóa đơn từ MySQL!", e);
        }
        return danhSach;
    }

    @Override
    public HoaDon findById(String id) {
        HoaDon hd = null;
        String sqlHoaDon = "SELECT * FROM hoa_don WHERE ma_hd=?";
        String sqlChiTiet = "SELECT * FROM chi_tiet_hoa_don WHERE ma_hd=?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement pstmtHD = conn.prepareStatement(sqlHoaDon)) {
                pstmtHD.setString(1, id);
                try (ResultSet rs = pstmtHD.executeQuery()) {
                    if (rs.next()) {
                        hd = new HoaDon();
                        hd.setMaHoaDon(rs.getString("ma_hd"));
                        hd.setTenKhachHang(rs.getString("ten_kh"));
                        hd.setNgayTao(rs.getTimestamp("ngay_tao"));
                    }
                }
            }

            if (hd != null) {
                try (PreparedStatement pstmtCT = conn.prepareStatement(sqlChiTiet)) {
                    pstmtCT.setString(1, id);
                    try (ResultSet rs = pstmtCT.executeQuery()) {
                        while (rs.next()) {
                            ChiTietHoaDon ct = new ChiTietHoaDon();
                            ct.setMaSanPham(rs.getString("ma_sp"));
                            ct.setSoLuong(rs.getInt("so_luong"));
                            ct.setDonGia(rs.getDouble("don_gia"));
                            hd.getDanhSachChiTiet().add(ct);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Không thể lấy chi tiết hóa đơn từ MySQL!", e);
        }
        return hd;
    }
}
