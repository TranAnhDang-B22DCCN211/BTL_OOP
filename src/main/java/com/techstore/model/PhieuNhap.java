package com.techstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhieuNhap {
    private String maPhieuNhap;
    private Date ngayNhap;
    private String nguoiNhap;
    private List<ChiTietPhieuNhap> danhSachChiTiet;

    public PhieuNhap() {
        this.danhSachChiTiet = new ArrayList<>();
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public String getNguoiNhap() {
        return nguoiNhap;
    }

    public void setNguoiNhap(String nguoiNhap) {
        this.nguoiNhap = nguoiNhap;
    }

    public List<ChiTietPhieuNhap> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietPhieuNhap> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }
}
