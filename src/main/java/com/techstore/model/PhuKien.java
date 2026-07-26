package com.techstore.model;

public class PhuKien extends SanPham {
    private String loaiKetNoi;

    public PhuKien() {
        super();
    }

    public PhuKien(String maSanPham, String tenSanPham, String hangSanXuat, double giaBan, int soLuongTon,
            String loaiKetNoi) {
        super(maSanPham, tenSanPham, hangSanXuat, giaBan, soLuongTon);
        this.loaiKetNoi = loaiKetNoi;
    }

    public String getLoaiKetNoi() {
        return loaiKetNoi;
    }

    public void setLoaiKetNoi(String loaiKetNoi) {
        this.loaiKetNoi = loaiKetNoi;
    }

    @Override
    public String getChiTietSanPham() {
        return "Phụ kiện - " + tenSanPham + " | Kết nối: " + loaiKetNoi;
    }
}
