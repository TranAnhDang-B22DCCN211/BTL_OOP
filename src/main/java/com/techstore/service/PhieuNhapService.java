package com.techstore.service;

import com.techstore.exception.TechStoreException;
import com.techstore.model.PhieuNhap;
import com.techstore.repository.PhieuNhapRepository;

import java.util.List;

public class PhieuNhapService {

    private final PhieuNhapRepository repo;

    public PhieuNhapService() {
        this.repo = new PhieuNhapRepository();
    }

    public List<PhieuNhap> layDanhSachPhieuNhap() {
        return repo.findAll();
    }

    public boolean taoPhieuNhap(PhieuNhap pn) throws TechStoreException {
        if (pn.getMaPhieuNhap() == null || pn.getMaPhieuNhap().trim().isEmpty()) {
            throw new TechStoreException("Mã phiếu nhập không được để trống!");
        }
        if (pn.getNguoiNhap() == null || pn.getNguoiNhap().trim().isEmpty()) {
            throw new TechStoreException("Người nhập không được để trống!");
        }
        if (pn.getDanhSachChiTiet() == null || pn.getDanhSachChiTiet().isEmpty()) {
            throw new TechStoreException("Phiếu nhập phải có ít nhất 1 sản phẩm!");
        }
        return repo.insert(pn);
    }
}
