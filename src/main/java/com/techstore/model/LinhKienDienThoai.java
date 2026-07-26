package com.techstore.model;

public class LinhKienDienThoai extends SanPham {
    private String dongMayHoTro;

    public LinhKienDienThoai() {
        super();
    }

    public LinhKienDienThoai(String maSanPham, String tenSanPham, String hangSanXuat, double giaBan, int soLuongTon,
            String dongMayHoTro) {
        super(maSanPham, tenSanPham, hangSanXuat, giaBan, soLuongTon);
        this.dongMayHoTro = dongMayHoTro;
    }

    public String getDongMayHoTro() {
        return dongMayHoTro;
    }

    public void setDongMayHoTro(String dongMayHoTro) {
        this.dongMayHoTro = dongMayHoTro;
    }

    @Override
    public String getChiTietSanPham() {
        return "Linh kiện ĐT - " + tenSanPham + " | Hỗ trợ máy: " + dongMayHoTro;
    }
}
