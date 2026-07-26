package com.techstore.model;

public class LinhKienPC extends SanPham {
    private String thongSoKyThuat;
    private int thoiGianBaoHanh;

    public LinhKienPC() {
        super();
    }

    public LinhKienPC(String maSanPham, String tenSanPham, String hangSanXuat, double giaBan, int soLuongTon,
            String thongSoKyThuat, int thoiGianBaoHanh) {
        super(maSanPham, tenSanPham, hangSanXuat, giaBan, soLuongTon);
        this.thongSoKyThuat = thongSoKyThuat;
        this.thoiGianBaoHanh = thoiGianBaoHanh;
    }

    public String getThongSoKyThuat() {
        return thongSoKyThuat;
    }

    public void setThongSoKyThuat(String thongSoKyThuat) {
        this.thongSoKyThuat = thongSoKyThuat;
    }

    public int getThoiGianBaoHanh() {
        return thoiGianBaoHanh;
    }

    public void setThoiGianBaoHanh(int thoiGianBaoHanh) {
        this.thoiGianBaoHanh = thoiGianBaoHanh;
    }

    @Override
    public String getChiTietSanPham() {
        return "Linh kiện PC - " + tenSanPham + " | Thông số: " + thongSoKyThuat + " | Bảo hành: " + thoiGianBaoHanh
                + " tháng";
    }
}
