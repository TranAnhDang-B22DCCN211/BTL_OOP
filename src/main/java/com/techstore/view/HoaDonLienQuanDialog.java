package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HoaDonLienQuanDialog extends JDialog {
    private JLabel lblTenSP, lblTongSl, lblTongTien;
    private JTable tblHoaDon;
    private DefaultTableModel tableModel;
    private JButton btnDong;

    public HoaDonLienQuanDialog(Frame owner) {
        super(owner, "Danh Sách Hóa Đơn Mua Sản Phẩm", true);
        setSize(780, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- PHẦN 1: THÔNG TIN SẢN PHẨM ---
        JPanel pnlTop = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông Tin Thống Kê Sản Phẩm"));

        lblTenSP = new JLabel("Sản Phẩm: ");
        lblTenSP.setFont(new Font("Arial", Font.BOLD, 14));
        lblTenSP.setForeground(new Color(0, 102, 204));

        lblTongSl = new JLabel("Tổng số lượng bán: 0");
        lblTongSl.setFont(new Font("Arial", Font.PLAIN, 13));

        lblTongTien = new JLabel("Tổng tiền thu về: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 13));
        lblTongTien.setForeground(new Color(40, 167, 69));

        pnlTop.add(lblTenSP);
        pnlTop.add(lblTongSl);
        pnlTop.add(lblTongTien);
        add(pnlTop, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DANH SÁCH HÓA ĐƠN ---
        String[] columns = { "Mã HĐ", "Tên Khách Hàng", "Ngày Tạo", "Số Lượng Mua", "Đơn Giá (VNĐ)", "Thành Tiền (VNĐ)" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHoaDon = new JTable(tableModel);
        tblHoaDon.setRowHeight(28);
        add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        // --- PHẦN 3: NÚT ĐÓNG ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnDong = new JButton("Đóng");
        btnDong.setBackground(new Color(108, 117, 125));
        btnDong.setForeground(Color.WHITE);
        btnDong.setPreferredSize(new Dimension(90, 32));
        pnlBottom.add(btnDong);
        add(pnlBottom, BorderLayout.SOUTH);

        btnDong.addActionListener(e -> dispose());
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JLabel getLblTenSP() {
        return lblTenSP;
    }

    public JLabel getLblTongSl() {
        return lblTongSl;
    }

    public JLabel getLblTongTien() {
        return lblTongTien;
    }
}
