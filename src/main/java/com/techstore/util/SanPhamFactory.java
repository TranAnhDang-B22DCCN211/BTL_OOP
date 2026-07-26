package com.techstore.util;

import com.techstore.model.LinhKienDienThoai;
import com.techstore.model.LinhKienPC;
import com.techstore.model.PhuKien;
import com.techstore.model.SanPham;

public class SanPhamFactory {
    public static final String LOAI_PC = "PC";
    public static final String LOAI_DIEN_THOAI = "DIEN_THOAI";
    public static final String LOAI_PHU_KIEN = "PHU_KIEN";

    private SanPhamFactory() {
    }

    public static SanPham taoSanPham(String loaiSanPham) {
        return switch (loaiSanPham) {
            case LOAI_PC -> new LinhKienPC();
            case LOAI_DIEN_THOAI -> new LinhKienDienThoai();
            case LOAI_PHU_KIEN -> new PhuKien();
            default -> null;
        };
    }
}
