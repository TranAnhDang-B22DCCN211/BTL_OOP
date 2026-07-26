package com.techstore.controller;

import com.techstore.model.ChiTietHoaDon;
import com.techstore.model.HoaDon;
import com.techstore.model.SanPham;
import com.techstore.service.HoaDonService;
import com.techstore.service.SanPhamService;
import com.techstore.view.MainFrame;
import com.techstore.view.ThongKeFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThongKeController {
    private final ThongKeFrame view;
    private final HoaDonService hoaDonService;
    private final SanPhamService sanPhamService;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public ThongKeController(ThongKeFrame view) {
        this.view = view;
        this.hoaDonService = new HoaDonService();
        this.sanPhamService = new SanPhamService();
        initController();
        thongKeDuLieu();
    }

    private void initController() {
        view.getBtnQuayLai().addActionListener(e -> {
            view.dispose();
            MainFrame mainFrame = new MainFrame();
            new MainController(mainFrame);
            mainFrame.setVisible(true);
        });

        view.getBtnLoc().addActionListener(e -> {
            Date tuNgay = view.getTuNgay();
            Date denNgay = view.getDenNgay();

            if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
                JOptionPane.showMessageDialog(view, "Lỗi: 'Từ ngày' không được lớn hơn 'Đến ngày'!");
                return;
            }

            thongKeDuLieu();
        });

        view.getRadDoanhThu().addActionListener(e -> thongKeDuLieu());
        view.getRadSoLuong().addActionListener(e -> thongKeDuLieu());
        view.getCboDanhMuc().addActionListener(e -> thongKeDuLieu());

        // Nút Xem Hóa Đơn Liên Quan ở góc dưới bên phải
        view.getBtnXemChiTiet().addActionListener(e -> moChiTietDuocChon());

        // Menu Xem Chi Tiết khi click ô Thao tác
        view.getItemXemChiTiet().addActionListener(e -> moChiTietDuocChon());

        // Sự kiện Click Chuột lên bảng Thống Kê
        view.getTblThongKe().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JTable table = view.getTblThongKe();
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                }

                if (col == 4 && row >= 0) {
                    view.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void moChiTietDuocChon() {
        int row = view.getTblThongKe().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn 1 sản phẩm từ bảng để xem danh sách hóa đơn liên quan!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maSp = view.getTableModel().getValueAt(row, 0).toString();
        String tenSp = view.getTableModel().getValueAt(row, 1).toString();

        hienThiHoaDonLienQuan(maSp, tenSp);
    }

    private void hienThiHoaDonLienQuan(String maSp, String tenSp) {
        Date tuNgay = view.getTuNgay();
        Date denNgay = view.getDenNgay();

        Calendar calTu = Calendar.getInstance();
        if (tuNgay != null) {
            calTu.setTime(tuNgay);
            calTu.set(Calendar.HOUR_OF_DAY, 0);
            calTu.set(Calendar.MINUTE, 0);
            calTu.set(Calendar.SECOND, 0);
            calTu.set(Calendar.MILLISECOND, 0);
            tuNgay = calTu.getTime();
        }

        Calendar calDen = Calendar.getInstance();
        if (denNgay != null) {
            calDen.setTime(denNgay);
            calDen.set(Calendar.HOUR_OF_DAY, 23);
            calDen.set(Calendar.MINUTE, 59);
            calDen.set(Calendar.SECOND, 59);
            calDen.set(Calendar.MILLISECOND, 999);
            denNgay = calDen.getTime();
        }

        com.techstore.view.HoaDonLienQuanDialog dialog = new com.techstore.view.HoaDonLienQuanDialog(view);
        dialog.getLblTenSP().setText("Sản Phẩm: [" + maSp + "] - " + tenSp);

        DefaultTableModel model = dialog.getTableModel();
        model.setRowCount(0);

        int tongSoLuong = 0;
        double tongTien = 0;

        List<HoaDon> danhSachHD = hoaDonService.layDanhSachHoaDon();
        for (HoaDon hd : danhSachHD) {
            Date ngayTao = hd.getNgayTao();
            if (ngayTao == null) continue;

            if (tuNgay != null && ngayTao.before(tuNgay)) continue;
            if (denNgay != null && ngayTao.after(denNgay)) continue;

            HoaDon chiTietHd = hoaDonService.layChiTietHoaDon(hd.getMaHoaDon());
            if (chiTietHd == null || chiTietHd.getDanhSachChiTiet() == null) continue;

            for (ChiTietHoaDon ct : chiTietHd.getDanhSachChiTiet()) {
                if (ct.getMaSanPham().equals(maSp)) {
                    double thanhTien = ct.getDonGia() * ct.getSoLuong();
                    tongSoLuong += ct.getSoLuong();
                    tongTien += thanhTien;

                    String ngayTaoStr = dateFormat.format(ngayTao);

                    model.addRow(new Object[] {
                            hd.getMaHoaDon(),
                            hd.getTenKhachHang(),
                            ngayTaoStr,
                            ct.getSoLuong(),
                            String.format("%,.0f", ct.getDonGia()),
                            String.format("%,.0f", thanhTien)
                    });
                }
            }
        }

        dialog.getLblTongSl().setText("Tổng số lượng bán: " + tongSoLuong + " cái");
        dialog.getLblTongTien().setText("Tổng tiền thu về: " + String.format("%,.0f", tongTien) + " VNĐ");

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view,
                    "Không tìm thấy hóa đơn nào mua sản phẩm [" + maSp + "] trong khoảng thời gian đã chọn!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        dialog.setVisible(true);
    }

    private void thongKeDuLieu() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        Date tuNgay = view.getTuNgay();
        Date denNgay = view.getDenNgay();

        // Set từ ngày về đầu ngày (00:00:00)
        Calendar calTu = Calendar.getInstance();
        if (tuNgay != null) {
            calTu.setTime(tuNgay);
            calTu.set(Calendar.HOUR_OF_DAY, 0);
            calTu.set(Calendar.MINUTE, 0);
            calTu.set(Calendar.SECOND, 0);
            calTu.set(Calendar.MILLISECOND, 0);
            tuNgay = calTu.getTime();
        }

        // Set đến ngày về cuối ngày (23:59:59)
        Calendar calDen = Calendar.getInstance();
        if (denNgay != null) {
            calDen.setTime(denNgay);
            calDen.set(Calendar.HOUR_OF_DAY, 23);
            calDen.set(Calendar.MINUTE, 59);
            calDen.set(Calendar.SECOND, 59);
            calDen.set(Calendar.MILLISECOND, 999);
            denNgay = calDen.getTime();
        }

        // 1. Lấy tất cả sản phẩm
        List<SanPham> allSanPhams = sanPhamService.layDanhSachSanPham();
        Map<String, SanPham> mapSp = new HashMap<>();
        for (SanPham sp : allSanPhams) {
            mapSp.put(sp.getMaSanPham(), sp);
        }

        // 2. Thống kê số lượng bán và tổng thu từng sản phẩm từ CSDL
        class ThongKeItem {
            String maSp;
            String tenSp;
            String loaiSp;
            int soLuongDaBan = 0;
            double tongThu = 0;
        }

        Map<String, ThongKeItem> itemsMap = new HashMap<>();

        List<HoaDon> danhSachHD = hoaDonService.layDanhSachHoaDon();
        for (HoaDon hd : danhSachHD) {
            Date ngayTao = hd.getNgayTao();
            if (ngayTao == null) continue;

            if (tuNgay != null && ngayTao.before(tuNgay)) continue;
            if (denNgay != null && ngayTao.after(denNgay)) continue;

            HoaDon chiTietHd = hoaDonService.layChiTietHoaDon(hd.getMaHoaDon());
            if (chiTietHd == null || chiTietHd.getDanhSachChiTiet() == null) continue;

            for (ChiTietHoaDon ct : chiTietHd.getDanhSachChiTiet()) {
                String maSp = ct.getMaSanPham();
                ThongKeItem item = itemsMap.computeIfAbsent(maSp, k -> {
                    ThongKeItem newItem = new ThongKeItem();
                    newItem.maSp = maSp;
                    SanPham sp = mapSp.get(maSp);
                    if (sp != null) {
                        newItem.tenSp = sp.getTenSanPham();
                        String loai = sp.getClass().getSimpleName();
                        newItem.loaiSp = switch (loai) {
                            case "LinhKienPC" -> "Linh Kiện PC";
                            case "LinhKienDienThoai" -> "Điện Thoại";
                            case "PhuKien" -> "Phụ Kiện";
                            default -> loai;
                        };
                    } else {
                        newItem.tenSp = maSp;
                        newItem.loaiSp = "Khác";
                    }
                    return newItem;
                });

                item.soLuongDaBan += ct.getSoLuong();
                item.tongThu += ct.getDonGia() * ct.getSoLuong();
            }
        }

        // 3. Lọc theo Danh mục
        String selectedDanhMuc = view.getCboDanhMuc().getSelectedItem().toString();
        List<ThongKeItem> resultList = new ArrayList<>();
        for (ThongKeItem item : itemsMap.values()) {
            if (selectedDanhMuc.equals("Tất cả") || item.loaiSp.equals(selectedDanhMuc)) {
                resultList.add(item);
            }
        }

        // 4. Sắp xếp danh sách
        boolean byDoanhThu = view.getRadDoanhThu().isSelected();
        if (byDoanhThu) {
            resultList.sort((a, b) -> Double.compare(b.tongThu, a.tongThu));
        } else {
            resultList.sort((a, b) -> Integer.compare(b.soLuongDaBan, a.soLuongDaBan));
        }

        // 5. Đổ lên bảng & Tính tổng doanh thu
        double tongDoanhThuTatCa = 0;
        for (ThongKeItem item : resultList) {
            model.addRow(new Object[] {
                    item.maSp,
                    item.tenSp,
                    item.soLuongDaBan,
                    String.format("%,.0f", item.tongThu),
                    "⋮"
            });
            tongDoanhThuTatCa += item.tongThu;
        }

        view.getLblTongDoanhThu().setText("Tổng Doanh Thu: " + String.format("%,.0f", tongDoanhThuTatCa) + " VNĐ");
    }
}
