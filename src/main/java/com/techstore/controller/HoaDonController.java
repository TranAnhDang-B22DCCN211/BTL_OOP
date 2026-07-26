package com.techstore.controller;

import com.techstore.exception.TechStoreException;
import com.techstore.model.ChiTietHoaDon;
import com.techstore.model.HoaDon;
import com.techstore.model.SanPham;
import com.techstore.service.HoaDonService;
import com.techstore.service.SanPhamService;
import com.techstore.view.ChiTietHoaDonDialog;
import com.techstore.view.ChonSanPhamDialog;
import com.techstore.view.HoaDonDialog;
import com.techstore.view.HoaDonFrame;
import com.techstore.view.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDonController {
    private final HoaDonFrame view;
    private final HoaDonService hoaDonService;
    private final SanPhamService sanPhamService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HoaDonController(HoaDonFrame view) {
        this.view = view;
        this.hoaDonService = new HoaDonService();
        this.sanPhamService = new SanPhamService();
        initController();
        loadData();
    }

    private void initController() {
        view.getBtnThemMoi().addActionListener(e -> moFormTaoHoaDon());
        view.getBtnQuayLai().addActionListener(e -> quayLaiMenu());
        view.getItemXem().addActionListener(e -> xemChiTietHoaDonDuocChon());
        view.getItemXoa().addActionListener(e -> xoaHoaDonDuocChon());

        view.getTblHoaDon().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiMenuThaoTac(e);
            }
        });
    }

    private void moFormTaoHoaDon() {
        HoaDonDialog dialog = new HoaDonDialog(view);
        dialog.getTxtMaHD().setText(sinhMaHoaDonTuDong());

        dialog.getBtnThemSanPham().addActionListener(event -> moDialogChonSanPham(dialog));
        dialog.getBtnXoaKhoiGio().addActionListener(event -> xoaSanPhamKhoiGio(dialog));
        dialog.getBtnLuu().addActionListener(event -> luuHoaDonMoi(dialog));

        dialog.setVisible(true);
    }

    private void moDialogChonSanPham(HoaDonDialog hoaDonDialog) {
        ChonSanPhamDialog chonDialog = new ChonSanPhamDialog(hoaDonDialog);
        DefaultTableModel modelKho = chonDialog.getTableModel();
        List<SanPham> danhSachSp = sanPhamService.layDanhSachSanPham();

        Runnable locDuLieu = () -> locSanPham(chonDialog, modelKho, danhSachSp);
        locDuLieu.run();

        chonDialog.getBtnTim().addActionListener(e -> locDuLieu.run());
        chonDialog.getBtnChon().addActionListener(e -> chonSanPhamVaoGioHang(chonDialog, hoaDonDialog, modelKho, danhSachSp));

        chonDialog.setVisible(true);
    }

    private void locSanPham(ChonSanPhamDialog chonDialog, DefaultTableModel modelKho, List<SanPham> danhSachSp) {
        modelKho.setRowCount(0);
        String tuKhoa = chonDialog.getTxtTimKiem().getText().trim().toLowerCase();
        String danhMuc = chonDialog.getCboDanhMuc().getSelectedItem().toString();

        for (SanPham sp : danhSachSp) {
            String loaiSpDisplay = layTenLoaiSanPham(sp);
            boolean matchTen = sp.getTenSanPham().toLowerCase().contains(tuKhoa)
                    || sp.getMaSanPham().toLowerCase().contains(tuKhoa);
            boolean matchLoai = danhMuc.equals("Tất cả") || loaiSpDisplay.equals(danhMuc);

            if (matchTen && matchLoai) {
                modelKho.addRow(new Object[] {
                        sp.getMaSanPham(),
                        sp.getTenSanPham(),
                        loaiSpDisplay,
                        String.format("%,.0f", sp.getGiaBan()),
                        sp.getSoLuongTon()
                });
            }
        }
    }

    private void chonSanPhamVaoGioHang(
            ChonSanPhamDialog chonDialog,
            HoaDonDialog hoaDonDialog,
            DefaultTableModel modelKho,
            List<SanPham> danhSachSp) {
        int row = chonDialog.getTblSanPham().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(chonDialog, "Vui lòng chọn 1 sản phẩm từ bảng!");
            return;
        }

        String input = JOptionPane.showInputDialog(
                chonDialog,
                "Nhập số lượng muốn mua cho SP: " + modelKho.getValueAt(row, 1),
                "1");

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int soLuongMua = Integer.parseInt(input.trim());
            int tonKho = Integer.parseInt(modelKho.getValueAt(row, 4).toString());

            if (soLuongMua <= 0) {
                JOptionPane.showMessageDialog(chonDialog, "Số lượng phải lớn hơn 0!");
                return;
            }
            if (soLuongMua > tonKho) {
                JOptionPane.showMessageDialog(chonDialog, "Không đủ hàng! Tồn kho chỉ còn: " + tonKho);
                return;
            }

            themDongGioHang(hoaDonDialog, modelKho, danhSachSp, row, soLuongMua);
            capNhatTongTien(hoaDonDialog);
            chonDialog.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(chonDialog, "Vui lòng nhập số hợp lệ!");
        }
    }

    private void themDongGioHang(HoaDonDialog dialog, DefaultTableModel modelKho, List<SanPham> danhSachSp, int row, int soLuongMua) {
        String maSp = modelKho.getValueAt(row, 0).toString();
        String tenSp = modelKho.getValueAt(row, 1).toString();
        double giaBan = timGiaSanPham(danhSachSp, maSp);
        double thanhTien = giaBan * soLuongMua;

        dialog.getModelGioHang().addRow(new Object[] {
                maSp,
                tenSp,
                String.format("%,.0f", giaBan),
                soLuongMua,
                String.format("%,.0f", thanhTien)
        });
    }

    private void xoaSanPhamKhoiGio(HoaDonDialog dialog) {
        int row = dialog.getTblGioHang().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng chọn sản phẩm trong giỏ để xóa!");
            return;
        }

        dialog.getModelGioHang().removeRow(row);
        capNhatTongTien(dialog);
    }

    private void luuHoaDonMoi(HoaDonDialog dialog) {
        String maHd = dialog.getTxtMaHD().getText().trim();
        String tenKh = dialog.getTxtTenKH().getText().trim();
        DefaultTableModel modelGio = dialog.getModelGioHang();

        if (!kiemTraDuLieuHoaDon(dialog, maHd, tenKh, modelGio)) {
            return;
        }

        try {
            HoaDon hd = taoHoaDonTuGioHang(maHd, tenKh, modelGio);
            if (hoaDonService.taoHoaDon(hd)) {
                JOptionPane.showMessageDialog(dialog, "Thêm hóa đơn " + maHd + " thành công!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi lưu hóa đơn vào CSDL!");
            }
        } catch (TechStoreException ex) {
            JOptionPane.showMessageDialog(dialog, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Có lỗi xảy ra: " + ex.getMessage());
        }
    }

    private boolean kiemTraDuLieuHoaDon(HoaDonDialog dialog, String maHd, String tenKh, DefaultTableModel modelGio) {
        if (maHd.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập Mã Hóa Đơn!");
            return false;
        }
        if (tenKh.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập Tên Khách Hàng!");
            dialog.getTxtTenKH().requestFocus();
            return false;
        }
        if (modelGio.getRowCount() == 0) {
            JOptionPane.showMessageDialog(dialog, "Giỏ hàng đang trống! Vui lòng chọn sản phẩm.");
            return false;
        }
        return true;
    }

    private HoaDon taoHoaDonTuGioHang(String maHd, String tenKh, DefaultTableModel modelGio) {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHd);
        hd.setTenKhachHang(tenKh);
        hd.setNgayTao(new Date());

        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        List<SanPham> allSp = sanPhamService.layDanhSachSanPham();
        for (int i = 0; i < modelGio.getRowCount(); i++) {
            String maSp = modelGio.getValueAt(i, 0).toString();

            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setMaSanPham(maSp);
            ct.setDonGia(timGiaSanPham(allSp, maSp));
            ct.setSoLuong(Integer.parseInt(modelGio.getValueAt(i, 3).toString()));
            chiTietList.add(ct);
        }

        hd.setDanhSachChiTiet(chiTietList);
        return hd;
    }

    private void quayLaiMenu() {
        view.dispose();
        MainFrame mainFrame = new MainFrame();
        new MainController(mainFrame);
        mainFrame.setVisible(true);
    }

    private void hienThiMenuThaoTac(MouseEvent e) {
        JTable table = view.getTblHoaDon();
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (row >= 0) {
            table.setRowSelectionInterval(row, row);
        }

        if (col == 3 && row >= 0) {
            view.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void xemChiTietHoaDonDuocChon() {
        int row = view.getTblHoaDon().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn hóa đơn cần xem!");
            return;
        }

        String maHD = view.getTableModel().getValueAt(row, 0).toString();
        HoaDon hd = hoaDonService.layChiTietHoaDon(maHD);
        if (hd == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy chi tiết cho hóa đơn: " + maHD);
            return;
        }

        hienThiChiTietHoaDon(hd);
    }

    private void hienThiChiTietHoaDon(HoaDon hd) {
        ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(view);
        dialog.getLblMaHD().setText("Mã HĐ: " + hd.getMaHoaDon());
        dialog.getLblTenKH().setText("Khách Hàng: " + hd.getTenKhachHang());
        dialog.getLblNgayTao().setText("Ngày Tạo: " + dinhDangNgay(hd.getNgayTao()));

        DefaultTableModel model = dialog.getTableModel();
        model.setRowCount(0);

        List<SanPham> sanPhamList = sanPhamService.layDanhSachSanPham();
        double tongTien = 0;

        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            double thanhTien = ct.getDonGia() * ct.getSoLuong();
            tongTien += thanhTien;

            model.addRow(new Object[] {
                    ct.getMaSanPham(),
                    timTenSanPham(sanPhamList, ct.getMaSanPham()),
                    String.format("%,.0f", ct.getDonGia()),
                    ct.getSoLuong(),
                    String.format("%,.0f", thanhTien)
            });
        }

        dialog.getLblTongTien().setText("Tổng tiền: " + String.format("%,.0f", tongTien) + " VNĐ");
        dialog.setVisible(true);
    }

    private void xoaHoaDonDuocChon() {
        int row = view.getTblHoaDon().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn hóa đơn cần xóa!");
            return;
        }

        String maHD = view.getTableModel().getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn xóa hóa đơn: " + maHD + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            thucHienXoaHoaDon(maHD);
        }
    }

    private void thucHienXoaHoaDon(String maHD) {
        try {
            if (hoaDonService.xoaHoaDon(maHD)) {
                JOptionPane.showMessageDialog(view, "Đã xóa hóa đơn " + maHD + " thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa hóa đơn!");
            }
        } catch (TechStoreException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Có lỗi xảy ra khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        List<HoaDon> danhSach = hoaDonService.layDanhSachHoaDon();
        for (HoaDon hd : danhSach) {
            model.addRow(new Object[] {
                    hd.getMaHoaDon(),
                    hd.getTenKhachHang(),
                    dinhDangNgay(hd.getNgayTao()),
                    "⋮"
            });
        }
    }

    private void capNhatTongTien(HoaDonDialog dialog) {
        double tong = 0;
        DefaultTableModel model = dialog.getModelGioHang();
        List<SanPham> sanPhamList = sanPhamService.layDanhSachSanPham();
        for (int i = 0; i < model.getRowCount(); i++) {
            String maSp = model.getValueAt(i, 0).toString();
            int soLuong = Integer.parseInt(model.getValueAt(i, 3).toString());
            tong += timGiaSanPham(sanPhamList, maSp) * soLuong;
        }

        dialog.getLblTongTien().setText("Tổng tiền: " + String.format("%,.0f", tong) + " VNĐ");
    }

    private double timGiaSanPham(List<SanPham> sanPhamList, String maSp) {
        return sanPhamList.stream()
                .filter(sp -> sp.getMaSanPham().equals(maSp))
                .findFirst()
                .map(SanPham::getGiaBan)
                .orElse(0.0);
    }

    private String timTenSanPham(List<SanPham> sanPhamList, String maSp) {
        return sanPhamList.stream()
                .filter(sp -> sp.getMaSanPham().equals(maSp))
                .map(SanPham::getTenSanPham)
                .findFirst()
                .orElse(maSp);
    }

    private String layTenLoaiSanPham(SanPham sp) {
        return switch (sp.getClass().getSimpleName()) {
            case "LinhKienPC" -> "Linh Kiện PC";
            case "LinhKienDienThoai" -> "Điện Thoại";
            case "PhuKien" -> "Phụ Kiện";
            default -> sp.getClass().getSimpleName();
        };
    }

    private String dinhDangNgay(Date ngay) {
        return ngay != null ? dateFormat.format(ngay) : "";
    }

    private String sinhMaHoaDonTuDong() {
        List<HoaDon> danhSach = hoaDonService.layDanhSachHoaDon();
        int maxNum = 0;

        for (HoaDon hd : danhSach) {
            String ma = hd.getMaHoaDon();
            if (ma != null && ma.startsWith("HD")) {
                try {
                    int num = Integer.parseInt(ma.substring(2).trim());
                    if (num < 10000 && num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return String.format("HD%03d", maxNum + 1);
    }
}
