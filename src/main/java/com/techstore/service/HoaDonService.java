package com.techstore.service;

import com.techstore.exception.TechStoreException;
import com.techstore.model.ChiTietHoaDon;
import com.techstore.model.HoaDon;
import com.techstore.model.SanPham;
import com.techstore.repository.HoaDonRepository;
import com.techstore.repository.SanPhamRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoaDonService {

    private final HoaDonRepository hoaDonRepo;
    private final SanPhamRepository sanPhamRepo;

    public HoaDonService() {
        this.hoaDonRepo = new HoaDonRepository();
        this.sanPhamRepo = new SanPhamRepository();
    }

    public List<HoaDon> layDanhSachHoaDon() {
        return hoaDonRepo.findAll();
    }

    public HoaDon layChiTietHoaDon(String maHd) {
        return hoaDonRepo.findById(maHd);
    }

    public boolean taoHoaDon(HoaDon hd) throws TechStoreException {
        if (hd.getMaHoaDon() == null || hd.getMaHoaDon().trim().isEmpty()) {
            throw new TechStoreException("Mã hóa đơn không được để trống!");
        }
        if (hd.getTenKhachHang() == null || hd.getTenKhachHang().trim().isEmpty()) {
            throw new TechStoreException("Tên khách hàng không được để trống!");
        }
        if (hd.getDanhSachChiTiet() == null || hd.getDanhSachChiTiet().isEmpty()) {
            throw new TechStoreException("Hóa đơn phải có ít nhất 1 sản phẩm!");
        }
        if (hoaDonRepo.findById(hd.getMaHoaDon()) != null) {
            throw new TechStoreException("Mã hóa đơn đã tồn tại trong hệ thống!");
        }

        kiemTraTonKho(hd);
        return hoaDonRepo.insert(hd);
    }

    private void kiemTraTonKho(HoaDon hd) throws TechStoreException {
        Map<String, Integer> tongSoLuongTheoSanPham = new HashMap<>();

        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            if (ct.getMaSanPham() == null || ct.getMaSanPham().trim().isEmpty()) {
                throw new TechStoreException("Mã sản phẩm trong hóa đơn không hợp lệ!");
            }
            if (ct.getSoLuong() <= 0) {
                throw new TechStoreException("Số lượng mua phải lớn hơn 0!");
            }

            tongSoLuongTheoSanPham.merge(ct.getMaSanPham(), ct.getSoLuong(), Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : tongSoLuongTheoSanPham.entrySet()) {
            SanPham sp = sanPhamRepo.findById(entry.getKey());
            if (sp == null) {
                throw new TechStoreException("Không tìm thấy sản phẩm: " + entry.getKey());
            }
            if (sp.getSoLuongTon() < entry.getValue()) {
                throw new TechStoreException("Không đủ tồn kho cho sản phẩm "
                        + entry.getKey() + ". Tồn kho hiện tại: " + sp.getSoLuongTon()
                        + ", số lượng mua: " + entry.getValue());
            }
        }
    }

    public boolean xoaHoaDon(String maHd) throws TechStoreException {
        if (maHd == null || maHd.trim().isEmpty()) {
            throw new TechStoreException("Mã hóa đơn không hợp lệ!");
        }
        return hoaDonRepo.delete(maHd);
    }
}
