package com.techstore.service;

import com.techstore.exception.SanPhamException;
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

    public boolean themSanPham(SanPham sp) throws SanPhamException {
        kiemTraDuLieuHopLe(sp);

        if (sanPhamRepo.findById(sp.getMaSanPham()) != null) {
            throw new SanPhamException("Mã sản phẩm đã tồn tại trong hệ thống!");
        }

        return sanPhamRepo.insert(sp);
    }

    public boolean capNhatSanPham(SanPham sp) throws SanPhamException {
        kiemTraDuLieuHopLe(sp);

        if (sanPhamRepo.findById(sp.getMaSanPham()) == null) {
            throw new SanPhamException("Không tìm thấy sản phẩm để cập nhật!");
        }

        return sanPhamRepo.update(sp);
    }

    public boolean xoaSanPham(String maSp) throws SanPhamException {
        if (maSp == null || maSp.trim().isEmpty()) {
            throw new SanPhamException("Mã sản phẩm không được để trống!");
        }
        return sanPhamRepo.delete(maSp);
    }

    private void kiemTraDuLieuHopLe(SanPham sp) throws SanPhamException {
        if (sp == null) {
            throw new SanPhamException("Sản phẩm không được null!");
        }
        if (sp.getMaSanPham() == null || sp.getMaSanPham().trim().isEmpty()) {
            throw new SanPhamException("Mã sản phẩm không được để trống!");
        }
        if (sp.getTenSanPham() == null || sp.getTenSanPham().trim().isEmpty()) {
            throw new SanPhamException("Tên sản phẩm không được để trống!");
        }
        if (sp.getGiaBan() <= 0) {
            throw new SanPhamException("Giá bán phải lớn hơn 0!");
        }
        if (sp.getSoLuongTon() < 0) {
            throw new SanPhamException("Số lượng tồn không hợp lệ!");
        }
    }
}
