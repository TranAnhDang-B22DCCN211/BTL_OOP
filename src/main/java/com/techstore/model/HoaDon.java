package com.techstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDon {
    private String maHoaDon;
    private String tenKhachHang;
    private Date ngayTao;
    private List<ChiTietHoaDon> danhSachChiTiet;

    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>();
    }

    public HoaDon(String maHoaDon, String tenKhachHang, Date ngayTao) {
        this.maHoaDon = maHoaDon;
        this.tenKhachHang = tenKhachHang;
        this.ngayTao = ngayTao;
        this.danhSachChiTiet = new ArrayList<>();
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public double getTongTien() {
        double tong = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            tong += ct.getThanhTien();
        }
        return tong;
    }
}
