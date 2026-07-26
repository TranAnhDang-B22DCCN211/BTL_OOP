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
        // 1. Nút Thêm Hóa Đơn Mới
        view.getBtnThemMoi().addActionListener(e -> {
            HoaDonDialog dialog = new HoaDonDialog(view);

            // Tự động sinh mã HĐ tăng dần dạng HD021, HD022...
            String autoMaHD = sinhMaHoaDonTuDong();
            dialog.getTxtMaHD().setText(autoMaHD);

            // Xử lý khi nhấn nút [+ Thêm Sản Phẩm]
            dialog.getBtnThemSanPham().addActionListener(event -> {
                ChonSanPhamDialog chonDialog = new ChonSanPhamDialog(dialog);
                DefaultTableModel modelKho = chonDialog.getTableModel();
                List<SanPham> danhSachSp = sanPhamService.layDanhSachSanPham();

                Runnable locDuLieu = () -> {
                    modelKho.setRowCount(0);
                    String tuKhoa = chonDialog.getTxtTimKiem().getText().trim().toLowerCase();
                    String danhMuc = chonDialog.getCboDanhMuc().getSelectedItem().toString();

                    for (SanPham sp : danhSachSp) {
                        String tenSp = sp.getTenSanPham().toLowerCase();
                        String loaiSp = sp.getClass().getSimpleName();

                        String loaiSpDisplay = switch (loaiSp) {
                            case "LinhKienPC" -> "Linh Kiện PC";
                            case "LinhKienDienThoai" -> "Điện Thoại";
                            case "PhuKien" -> "Phụ Kiện";
                            default -> loaiSp;
                        };

                        boolean matchTen = tenSp.contains(tuKhoa) || sp.getMaSanPham().toLowerCase().contains(tuKhoa);
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
                };

                locDuLieu.run();
                chonDialog.getBtnTim().addActionListener(eTim -> locDuLieu.run());

                // Chọn sản phẩm đưa vào Giỏ Hàng
                chonDialog.getBtnChon().addActionListener(eChon -> {
                    int row = chonDialog.getTblSanPham().getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(chonDialog, "Vui lòng chọn 1 sản phẩm từ bảng!");
                        return;
                    }

                    String input = JOptionPane.showInputDialog(chonDialog,
                            "Nhập số lượng muốn mua cho SP: " + modelKho.getValueAt(row, 1), "1");

                    if (input != null && !input.trim().isEmpty()) {
                        try {
                            int slMua = Integer.parseInt(input);
                            int tonKho = Integer.parseInt(modelKho.getValueAt(row, 4).toString());

                            if (slMua <= 0) {
                                JOptionPane.showMessageDialog(chonDialog, "Số lượng phải lớn hơn 0!");
                                return;
                            }
                            if (slMua > tonKho) {
                                JOptionPane.showMessageDialog(chonDialog, "Không đủ hàng! Tồn kho chỉ còn: " + tonKho);
                                return;
                            }

                            String ma = modelKho.getValueAt(row, 0).toString();
                            String ten = modelKho.getValueAt(row, 1).toString();
                            double gia = danhSachSp.stream()
                                    .filter(s -> s.getMaSanPham().equals(ma))
                                    .findFirst()
                                    .map(SanPham::getGiaBan)
                                    .orElse(0.0);
                            double thanhTien = gia * slMua;

                            dialog.getModelGioHang().addRow(new Object[] {
                                    ma,
                                    ten,
                                    String.format("%,.0f", gia),
                                    slMua,
                                    String.format("%,.0f", thanhTien)
                            });

                            capNhatTongTien(dialog);
                            chonDialog.dispose();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(chonDialog, "Vui lòng nhập số hợp lệ!");
                        }
                    }
                });

                chonDialog.setVisible(true);
            });

            // Xóa khỏi giỏ hàng
            dialog.getBtnXoaKhoiGio().addActionListener(event -> {
                int row = dialog.getTblGioHang().getSelectedRow();
                if (row != -1) {
                    dialog.getModelGioHang().removeRow(row);
                    capNhatTongTien(dialog);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn sản phẩm trong giỏ để xóa!");
                }
            });

            // Lưu hóa đơn mới vào DB
            dialog.getBtnLuu().addActionListener(event -> {
                String maHd = dialog.getTxtMaHD().getText().trim();
                String tenKh = dialog.getTxtTenKH().getText().trim();

                if (maHd.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập Mã Hóa Đơn!");
                    return;
                }
                if (tenKh.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập Tên Khách Hàng!");
                    dialog.getTxtTenKH().requestFocus();
                    return;
                }
                DefaultTableModel modelGio = dialog.getModelGioHang();
                if (modelGio.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Giỏ hàng đang trống! Vui lòng chọn sản phẩm.");
                    return;
                }

                try {
                    HoaDon hd = new HoaDon();
                    hd.setMaHoaDon(maHd);
                    hd.setTenKhachHang(tenKh);
                    hd.setNgayTao(new Date());

                    List<ChiTietHoaDon> chiTietList = new ArrayList<>();
                    List<SanPham> allSp = sanPhamService.layDanhSachSanPham();
                    for (int i = 0; i < modelGio.getRowCount(); i++) {
                        String maSp = modelGio.getValueAt(i, 0).toString();
                        double gia = allSp.stream()
                                .filter(s -> s.getMaSanPham().equals(maSp))
                                .findFirst()
                                .map(SanPham::getGiaBan)
                                .orElse(0.0);

                        ChiTietHoaDon ct = new ChiTietHoaDon();
                        ct.setMaSanPham(maSp);
                        ct.setDonGia(gia);
                        ct.setSoLuong(Integer.parseInt(modelGio.getValueAt(i, 3).toString()));
                        chiTietList.add(ct);
                    }
                    hd.setDanhSachChiTiet(chiTietList);

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
            });

            dialog.setVisible(true);
        });

        // 2. Nút Quay Lại
        view.getBtnQuayLai().addActionListener(e -> {
            view.dispose();
            MainFrame mainFrame = new MainFrame();
            new MainController(mainFrame);
            mainFrame.setVisible(true);
        });

        // 3. Sự kiện Click Chuột lên Bảng Hóa Đơn để mở Popup Menu
        view.getTblHoaDon().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
        });

        // 4. Menu Xem Chi Tiết Hóa Đơn
        view.getItemXem().addActionListener(e -> {
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

            ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(view);
            dialog.getLblMaHD().setText("Mã HĐ: " + hd.getMaHoaDon());
            dialog.getLblTenKH().setText("Khách Hàng: " + hd.getTenKhachHang());
            dialog.getLblNgayTao().setText("Ngày Tạo: " + (hd.getNgayTao() != null ? dateFormat.format(hd.getNgayTao()) : ""));

            DefaultTableModel model = dialog.getTableModel();
            model.setRowCount(0);

            List<SanPham> sanPhamList = sanPhamService.layDanhSachSanPham();
            double tongTien = 0;

            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                String maSp = ct.getMaSanPham();
                String tenSp = sanPhamList.stream()
                        .filter(sp -> sp.getMaSanPham().equals(maSp))
                        .map(SanPham::getTenSanPham)
                        .findFirst()
                        .orElse(maSp);

                double thanhTien = ct.getDonGia() * ct.getSoLuong();
                tongTien += thanhTien;

                model.addRow(new Object[] {
                        maSp,
                        tenSp,
                        String.format("%,.0f", ct.getDonGia()),
                        ct.getSoLuong(),
                        String.format("%,.0f", thanhTien)
                });
            }

            dialog.getLblTongTien().setText("Tổng tiền: " + String.format("%,.0f", tongTien) + " VNĐ");
            dialog.setVisible(true);
        });

        // 5. Menu Xóa Hóa Đơn
        view.getItemXoa().addActionListener(e -> {
            int row = view.getTblHoaDon().getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn hóa đơn cần xóa!");
                return;
            }

            String maHD = view.getTableModel().getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn xóa hóa đơn: " + maHD + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (hoaDonService.xoaHoaDon(maHD)) {
                        JOptionPane.showMessageDialog(view, "Đã xóa hóa đơn " + maHD + " thành công!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi khi xóa hóa đơn!");
                    }
                } catch (TechStoreException ex) {
                    JOptionPane.showMessageDialog(view, ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Có lỗi xảy ra khi xóa: " + ex.getMessage());
                }
            }
        });
    }

    private void loadData() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        List<HoaDon> danhSach = hoaDonService.layDanhSachHoaDon();
        for (HoaDon hd : danhSach) {
            String ngayTaoStr = (hd.getNgayTao() != null) ? dateFormat.format(hd.getNgayTao()) : "";
            model.addRow(new Object[] {
                    hd.getMaHoaDon(),
                    hd.getTenKhachHang(),
                    ngayTaoStr,
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
            int sl = Integer.parseInt(model.getValueAt(i, 3).toString());
            double gia = sanPhamList.stream()
                    .filter(sp -> sp.getMaSanPham().equals(maSp))
                    .findFirst()
                    .map(SanPham::getGiaBan)
                    .orElse(0.0);
            tong += gia * sl;
        }
        dialog.getLblTongTien().setText("Tổng tiền: " + String.format("%,.0f", tong) + " VNĐ");
    }

    private String sinhMaHoaDonTuDong() {
        List<HoaDon> danhSach = hoaDonService.layDanhSachHoaDon();
        int maxNum = 0;

        for (HoaDon hd : danhSach) {
            String ma = hd.getMaHoaDon();
            if (ma != null && ma.startsWith("HD")) {
                try {
                    String numPart = ma.substring(2).trim();
                    int num = Integer.parseInt(numPart);
                    if (num < 10000 && num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        int nextNum = maxNum + 1;
        return String.format("HD%03d", nextNum);
    }
}
