package com.techstore.service;

import com.techstore.exception.TechStoreException;
import com.techstore.model.SanPham;
import com.techstore.repository.SanPhamRepository;
import java.util.List;

public class SanPhamService {

    private final SanPhamRepository sanPhamRepo;

    public SanPhamService() {
        this.sanPhamRepo = new SanPhamRepository();
    }

    public List<SanPham> layDanhSachSanPham() {
        return sanPhamRepo.findAll();
    }

    public boolean themSanPham(SanPham sp) throws TechStoreException {
        kiemTraDuLieuHopLe(sp);

        if (sanPhamRepo.findById(sp.getMaSanPham()) != null) {
            throw new TechStoreException("Mã sản phẩm đã tồn tại trong hệ thống!");
        }

        return sanPhamRepo.insert(sp);
    }

    public boolean capNhatSanPham(SanPham sp) throws TechStoreException {
        kiemTraDuLieuHopLe(sp);

        if (sanPhamRepo.findById(sp.getMaSanPham()) == null) {
            throw new TechStoreException("Không tìm thấy sản phẩm để cập nhật!");
        }

        return sanPhamRepo.update(sp);
    }

    public boolean xoaSanPham(String maSp) throws TechStoreException {
        if (maSp == null || maSp.trim().isEmpty()) {
            throw new TechStoreException("Mã sản phẩm không được để trống!");
        }
        return sanPhamRepo.delete(maSp);
    }

    private void kiemTraDuLieuHopLe(SanPham sp) throws TechStoreException {
        if (sp == null) {
            throw new TechStoreException("Sản phẩm không được null!");
        }
        if (sp.getMaSanPham() == null || sp.getMaSanPham().trim().isEmpty()) {
            throw new TechStoreException("Mã sản phẩm không được để trống!");
        }
        if (sp.getTenSanPham() == null || sp.getTenSanPham().trim().isEmpty()) {
            throw new TechStoreException("Tên sản phẩm không được để trống!");
        }
        if (sp.getGiaBan() <= 0) {
            throw new TechStoreException("Giá bán phải lớn hơn 0!");
        }
        if (sp.getSoLuongTon() < 0) {
            throw new TechStoreException("Số lượng tồn không hợp lệ!");
        }
    }
}
