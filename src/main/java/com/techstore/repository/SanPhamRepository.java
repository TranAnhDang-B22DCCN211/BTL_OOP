package com.techstore.repository;

import com.techstore.exception.DatabaseException;
import com.techstore.model.SanPham;
import com.techstore.util.SanPhamFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SanPhamRepository implements BaseRepository<SanPham> {

    @Override
    public boolean insert(SanPham entity) {
        String sql = "INSERT INTO san_pham (ma_sp, ten_sp, hang_sx, gia_ban, so_luong, loai_sp) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getMaSanPham());
            pstmt.setString(2, entity.getTenSanPham());
            pstmt.setString(3, entity.getHangSanXuat());
            pstmt.setDouble(4, entity.getGiaBan());
            pstmt.setInt(5, entity.getSoLuongTon());

            String loaiSp = entity.getClass().getSimpleName();
            pstmt.setString(6, loaiSp);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Không thể thêm sản phẩm vào MySQL!", e);
        }
    }

    @Override
    public boolean update(SanPham entity) {
        String sql = "UPDATE san_pham SET ten_sp=?, hang_sx=?, gia_ban=?, so_luong=? WHERE ma_sp=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getTenSanPham());
            pstmt.setString(2, entity.getHangSanXuat());
            pstmt.setDouble(3, entity.getGiaBan());
            pstmt.setInt(4, entity.getSoLuongTon());
            pstmt.setString(5, entity.getMaSanPham());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Không thể cập nhật sản phẩm trong MySQL!", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM san_pham WHERE ma_sp=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Không thể xóa sản phẩm khỏi MySQL!", e);
        }
    }

    @Override
    public List<SanPham> findAll() {
        List<SanPham> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM san_pham";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String loaiSp = rs.getString("loai_sp");
                SanPham sp = createSanPhamByLoai(loaiSp);

                if (sp != null) {
                    sp.setMaSanPham(rs.getString("ma_sp"));
                    sp.setTenSanPham(rs.getString("ten_sp"));
                    sp.setHangSanXuat(rs.getString("hang_sx"));
                    sp.setGiaBan(rs.getDouble("gia_ban"));
                    sp.setSoLuongTon(rs.getInt("so_luong"));
                    danhSach.add(sp);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Không thể lấy danh sách sản phẩm từ MySQL!", e);
        }
        return danhSach;
    }

    @Override
    public SanPham findById(String id) {
        String sql = "SELECT * FROM san_pham WHERE ma_sp=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String loaiSp = rs.getString("loai_sp");
                    SanPham sp = createSanPhamByLoai(loaiSp);

                    if (sp != null) {
                        sp.setMaSanPham(rs.getString("ma_sp"));
                        sp.setTenSanPham(rs.getString("ten_sp"));
                        sp.setHangSanXuat(rs.getString("hang_sx"));
                        sp.setGiaBan(rs.getDouble("gia_ban"));
                        sp.setSoLuongTon(rs.getInt("so_luong"));
                        return sp;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Không thể tìm sản phẩm trong MySQL!", e);
        }
        return null;
    }

    private SanPham createSanPhamByLoai(String loaiSp) {
        if (loaiSp == null) {
            return null;
        }

        return switch (loaiSp) {
            case "LinhKienPC" -> SanPhamFactory.taoSanPham(SanPhamFactory.LOAI_PC);
            case "LinhKienDienThoai" -> SanPhamFactory.taoSanPham(SanPhamFactory.LOAI_DIEN_THOAI);
            case "PhuKien" -> SanPhamFactory.taoSanPham(SanPhamFactory.LOAI_PHU_KIEN);
            default -> null;
        };
    }
}
