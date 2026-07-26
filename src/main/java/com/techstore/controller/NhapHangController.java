package com.techstore.controller;

import com.techstore.model.ChiTietPhieuNhap;
import com.techstore.model.PhieuNhap;
import com.techstore.model.SanPham;
import com.techstore.service.PhieuNhapService;
import com.techstore.service.SanPhamService;
import com.techstore.view.MainFrame;
import com.techstore.view.NhapHangFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhapHangController {
    private final NhapHangFrame view;
    private final PhieuNhapService phieuNhapService;
    private final SanPhamService sanPhamService;

    public NhapHangController(NhapHangFrame view) {
        this.view = view;
        this.phieuNhapService = new PhieuNhapService();
        this.sanPhamService = new SanPhamService();
        initController();
    }

    private void initController() {
        view.getBtnQuayLai().addActionListener(e -> {
            view.dispose();
            MainFrame mainFrame = new MainFrame();
            new MainController(mainFrame);
            mainFrame.setVisible(true);
        });

        view.getBtnImport().addActionListener(e -> xuLyImportFile());
        view.getBtnLuuCSDL().addActionListener(e -> xuLyLuuCSDL());
    }

    private void xuLyImportFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file danh sách nhập hàng");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text or CSV files", "txt", "csv"));

        int userSelection = fileChooser.showOpenDialog(view);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRead = fileChooser.getSelectedFile();
            docFileVaHienThi(fileToRead);
        }
    }

    private void docFileVaHienThi(File file) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        List<SanPham> sanPhamList = sanPhamService.layDanhSachSanPham();
        Map<String, String> mapTenSp = new HashMap<>();
        for (SanPham sp : sanPhamList) {
            mapTenSp.put(sp.getMaSanPham(), sp.getTenSanPham());
        }

        int soDongThanhCong = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length == 2) {
                    String maSp = data[0].trim();
                    String soLuongStr = data[1].trim();

                    try {
                        int soLuong = Integer.parseInt(soLuongStr);
                        if (soLuong <= 0) {
                            throw new NumberFormatException();
                        }

                        String tenSp = mapTenSp.getOrDefault(maSp, "Không tìm thấy tên SP");

                        model.addRow(new Object[] { maSp, tenSp, soLuong });
                        soDongThanhCong++;
                    } catch (NumberFormatException ex) {
                        model.addRow(new Object[] { maSp, "Lỗi số lượng", soLuongStr });
                    }
                } else {
                    model.addRow(new Object[] { line, "Lỗi định dạng", "" });
                }
            }

            if (soDongThanhCong > 0) {
                JOptionPane.showMessageDialog(view,
                        "Đã đọc " + soDongThanhCong + " sản phẩm từ file.\nVui lòng kiểm tra danh sách và nhấn nút '💾 Lưu Vào CSDL' để xác nhận.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Không có dữ liệu hợp lệ trong file!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi đọc file: " + ex.getMessage());
        }
    }

    private void xuLyLuuCSDL() {
        DefaultTableModel model = view.getTableModel();
        int rowCount = model.getRowCount();

        if (rowCount == 0) {
            JOptionPane.showMessageDialog(view,
                    "Danh sách nhập hàng đang trống! Vui lòng bấm 'Chọn File Nhập Hàng' trước.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc chắn muốn lưu " + rowCount + " sản phẩm này vào CSDL và cập nhật tồn kho kho hàng?",
                "Xác nhận lưu CSDL",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String maPhieuNhap = "PN" + (System.currentTimeMillis() % 100000);
        PhieuNhap pn = new PhieuNhap();
        pn.setMaPhieuNhap(maPhieuNhap);
        pn.setNgayNhap(new Date());
        pn.setNguoiNhap("Admin");

        List<ChiTietPhieuNhap> danhSachChiTiet = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            try {
                String maSp = model.getValueAt(i, 0).toString();
                int soLuong = Integer.parseInt(model.getValueAt(i, 2).toString());

                ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
                ct.setMaSanPham(maSp);
                ct.setSoLuongNhap(soLuong);
                danhSachChiTiet.add(ct);
            } catch (Exception ex) {
                // Bỏ qua nếu có dòng lỗi
            }
        }

        if (danhSachChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Không có sản phẩm hợp lệ nào để lưu!");
            return;
        }

        try {
            pn.setDanhSachChiTiet(danhSachChiTiet);
            if (phieuNhapService.taoPhieuNhap(pn)) {
                JOptionPane.showMessageDialog(view,
                        "Nhập hàng thành công! Đã lưu phiếu " + maPhieuNhap + " và tự động cập nhật kho trong CSDL.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                model.setRowCount(0); // Dọn sạch bảng sau khi đã lưu thành công
            } else {
                JOptionPane.showMessageDialog(view, "Lỗi khi lưu phiếu nhập hàng vào CSDL!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Có lỗi xảy ra khi lưu: " + ex.getMessage());
        }
    }
}
