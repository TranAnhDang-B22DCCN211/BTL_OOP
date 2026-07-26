package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class ThongKeFrame extends JFrame {
    private JTable tblThongKe;
    private DefaultTableModel tableModel;
    private JButton btnQuayLai, btnLoc, btnXemChiTiet;
    private JLabel lblTongDoanhThu;
    private JRadioButton radDoanhThu, radSoLuong;
    private JComboBox<String> cboDanhMuc;
    private JSpinner spinTuNgay, spinDenNgay;
    private JPopupMenu popupMenu;
    private JMenuItem itemXemChiTiet;

    public ThongKeFrame() {
        setTitle("Thống Kê Doanh Thu");
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PHẦN 1: TỔNG QUAN ---
        JPanel topPanel = new JPanel(new GridLayout(1, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel pnlDoanhThu = new JPanel(new BorderLayout());
        pnlDoanhThu.setBackground(new Color(40, 167, 69));
        pnlDoanhThu.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        lblTongDoanhThu = new JLabel("Tổng Doanh Thu: 0 VNĐ", SwingConstants.CENTER);
        lblTongDoanhThu.setForeground(Color.WHITE);
        lblTongDoanhThu.setFont(new Font("Arial", Font.BOLD, 22));
        pnlDoanhThu.add(lblTongDoanhThu, BorderLayout.CENTER);

        topPanel.add(pnlDoanhThu);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        // 2.1 Thanh công cụ Lọc (Filter)
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        pnlFilter.setBorder(BorderFactory.createTitledBorder("Tùy chọn Thống Kê"));

        radDoanhThu = new JRadioButton("Theo Doanh Thu", true);
        radSoLuong = new JRadioButton("Theo Số Lượng Bán");
        ButtonGroup bgSort = new ButtonGroup();
        bgSort.add(radDoanhThu);
        bgSort.add(radSoLuong);
        pnlFilter.add(radDoanhThu);
        pnlFilter.add(radSoLuong);

        pnlFilter.add(new JLabel(" | Danh mục:"));
        String[] danhMuc = { "Tất cả", "Điện Thoại", "Linh Kiện PC", "Phụ Kiện" };
        cboDanhMuc = new JComboBox<>(danhMuc);
        pnlFilter.add(cboDanhMuc);

        pnlFilter.add(new JLabel(" | Từ ngày:"));
        java.util.Calendar calTu = java.util.Calendar.getInstance();
        calTu.add(java.util.Calendar.DAY_OF_MONTH, -90);
        spinTuNgay = new JSpinner(new SpinnerDateModel(calTu.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spinTuNgay.setEditor(new JSpinner.DateEditor(spinTuNgay, "dd/MM/yyyy"));
        pnlFilter.add(spinTuNgay);

        pnlFilter.add(new JLabel("Đến:"));
        spinDenNgay = new JSpinner(new SpinnerDateModel());
        spinDenNgay.setEditor(new JSpinner.DateEditor(spinDenNgay, "dd/MM/yyyy"));
        pnlFilter.add(spinDenNgay);

        btnLoc = new JButton("Lọc Dữ Liệu");
        btnLoc.setBackground(new Color(23, 162, 184));
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setFont(new Font("Arial", Font.BOLD, 12));
        pnlFilter.add(btnLoc);

        centerPanel.add(pnlFilter, BorderLayout.NORTH);

        String[] columns = { "Mã SP", "Tên Sản Phẩm", "Số Lượng Đã Bán", "Tổng Thu (VNĐ)", "Thao tác" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblThongKe = new JTable(tableModel);
        tblThongKe.setRowHeight(30);
        centerPanel.add(new JScrollPane(tblThongKe), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- PHẦN BẢNG POPUP ---
        popupMenu = new JPopupMenu();
        itemXemChiTiet = new JMenuItem("👁 Xem Danh Sách Hóa Đơn Mua SP Này");
        popupMenu.add(itemXemChiTiet);

        // --- PHẦN NÚT BẤM BÊN DƯỚI ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        btnQuayLai = new JButton("⬅ Quay lại Menu");
        btnQuayLai.setBackground(new Color(108, 117, 125));
        btnQuayLai.setForeground(Color.WHITE);
        bottomPanel.add(btnQuayLai, BorderLayout.WEST);

        btnXemChiTiet = new JButton("👁 Xem Hóa Đơn Liên Quan");
        btnXemChiTiet.setBackground(new Color(0, 123, 255));
        btnXemChiTiet.setForeground(Color.WHITE);
        btnXemChiTiet.setFont(new Font("Arial", Font.BOLD, 13));
        bottomPanel.add(btnXemChiTiet, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public JButton getBtnQuayLai() {
        return btnQuayLai;
    }

    public JButton getBtnLoc() {
        return btnLoc;
    }

    public JButton getBtnXemChiTiet() {
        return btnXemChiTiet;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTblThongKe() {
        return tblThongKe;
    }

    public JLabel getLblTongDoanhThu() {
        return lblTongDoanhThu;
    }

    public JRadioButton getRadDoanhThu() {
        return radDoanhThu;
    }

    public JRadioButton getRadSoLuong() {
        return radSoLuong;
    }

    public JComboBox<String> getCboDanhMuc() {
        return cboDanhMuc;
    }

    public Date getTuNgay() {
        return (Date) spinTuNgay.getValue();
    }

    public Date getDenNgay() {
        return (Date) spinDenNgay.getValue();
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JMenuItem getItemXemChiTiet() {
        return itemXemChiTiet;
    }
}
