package com.techstore.controller;

import com.techstore.exception.TechStoreException;
import com.techstore.model.SanPham;
import com.techstore.service.SanPhamService;
import com.techstore.view.MainFrame;
import com.techstore.view.SanPhamDialog;
import com.techstore.view.SanPhamFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SanPhamController {
    private final SanPhamFrame view;
    private final SanPhamService sanPhamService;

    public SanPhamController(SanPhamFrame view) {
        this.view = view;
        this.sanPhamService = new SanPhamService();
        initController();
        loadData();
    }

    private void initController() {
        view.getTblSanPham().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = view.getTblSanPham();
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                }

                if (col == 6 && row >= 0) {
                    view.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        view.getItemSua().addActionListener(e -> {
            int row = view.getTblSanPham().getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần sửa!");
                return;
            }

            String maSp = view.getTableModel().getValueAt(row, 0).toString();
            List<SanPham> danhSach = sanPhamService.layDanhSachSanPham();
            SanPham sp = danhSach.stream()
                    .filter(s -> s.getMaSanPham().equals(maSp))
                    .findFirst()
                    .orElse(null);

            if (sp == null) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin sản phẩm!");
                return;
            }

            SanPhamDialog dialog = new SanPhamDialog(view, "Sửa Thông Tin Sản Phẩm");

            // Đổ dữ liệu hiện tại vào Form
            dialog.getTxtMaSp().setText(sp.getMaSanPham());
            dialog.getTxtMaSp().setEditable(false); // Không cho sửa Mã SP (Khóa chính)

            dialog.getTxtTenSp().setText(sp.getTenSanPham());
            dialog.getTxtHangSx().setText(sp.getHangSanXuat());
            dialog.getTxtGiaBan().setText(String.format("%.0f", sp.getGiaBan()));
            dialog.getTxtSoLuong().setText(String.valueOf(sp.getSoLuongTon()));

            String loaiSpStr = switch (sp.getClass().getSimpleName()) {
                case "LinhKienPC" -> "Linh Kiện PC";
                case "LinhKienDienThoai" -> "Điện Thoại";
                case "PhuKien" -> "Phụ Kiện";
                default -> "Linh Kiện PC";
            };
            dialog.getCboLoaiSp().setSelectedItem(loaiSpStr);
            dialog.getCboLoaiSp().setEnabled(false); // Loại sản phẩm cố định khi sửa

            // Xử lý khi nhấn nút Lưu trong Form Sửa
            dialog.getBtnLuu().addActionListener(event -> {
                try {
                    String tenSp = dialog.getTxtTenSp().getText().trim();
                    String hangSx = dialog.getTxtHangSx().getText().trim();
                    String giaBanStr = dialog.getTxtGiaBan().getText().trim();
                    String soLuongStr = dialog.getTxtSoLuong().getText().trim();

                    if (tenSp.isEmpty() || hangSx.isEmpty() || giaBanStr.isEmpty() || soLuongStr.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!");
                        return;
                    }

                    double giaBan = Double.parseDouble(giaBanStr);
                    int soLuong = Integer.parseInt(soLuongStr);

                    sp.setTenSanPham(tenSp);
                    sp.setHangSanXuat(hangSx);
                    sp.setGiaBan(giaBan);
                    sp.setSoLuongTon(soLuong);

                    if (sanPhamService.capNhatSanPham(sp)) {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật sản phẩm thành công!");
                        dialog.dispose();
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật sản phẩm!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá bán và Số lượng phải là số hợp lệ!");
                } catch (TechStoreException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Có lỗi xảy ra: " + ex.getMessage());
                }
            });

            dialog.setVisible(true);
        });

        view.getItemXoa().addActionListener(e -> {
            int row = view.getTblSanPham().getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view,
                        "Vui lòng chọn sản phẩm cần xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maSp = view.getTableModel().getValueAt(row, 0).toString();
            String tenSp = view.getTableModel().getValueAt(row, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn xóa sản phẩm:\n"
                            + "- Mã SP: " + maSp + "\n"
                            + "- Tên SP: " + tenSp + "?\n\n"
                            + "Lưu ý: Hành động này không thể hoàn tác!",
                    "Xác nhận xóa sản phẩm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (sanPhamService.xoaSanPham(maSp)) {
                        JOptionPane.showMessageDialog(view,
                                "Đã xóa thành công sản phẩm: " + maSp,
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(view,
                                "Lỗi: Không thể xóa sản phẩm khỏi cơ sở dữ liệu!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (TechStoreException ex) {
                    JOptionPane.showMessageDialog(view, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view,
                            "Có lỗi xảy ra khi xóa: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 5. Xử lý nút Quay lại
        view.getBtnQuayLai().addActionListener(e -> {
            view.dispose();
            MainFrame mainFrame = new MainFrame();
            new MainController(mainFrame);
            mainFrame.setVisible(true);
        });
    }

    private void loadData() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        List<SanPham> danhSach = sanPhamService.layDanhSachSanPham();
        for (SanPham sp : danhSach) {
            model.addRow(new Object[] {
                    sp.getMaSanPham(),
                    sp.getTenSanPham(),
                    sp.getHangSanXuat(),
                    String.format("%,.0f", sp.getGiaBan()),
                    sp.getSoLuongTon(),
                    sp.getClass().getSimpleName(),
                    "⋮"
            });
        }
    }

}
