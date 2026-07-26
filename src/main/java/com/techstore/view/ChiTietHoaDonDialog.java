package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ChiTietHoaDonDialog extends JDialog {
    private JLabel lblMaHD, lblTenKH, lblNgayTao, lblTongTien;
    private JTable tblChiTiet;
    private DefaultTableModel tableModel;
    private JButton btnDong;

    public ChiTietHoaDonDialog(Frame owner) {
        super(owner, "Chi Tiết Hóa Đơn", true);
        setSize(700, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- PHẦN 1: THÔNG TIN CHUNG ---
        JPanel pnlTop = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông Tin Hóa Đơn"));

        lblMaHD = new JLabel("Mã HĐ: ");
        lblMaHD.setFont(new Font("Arial", Font.BOLD, 13));

        lblTenKH = new JLabel("Khách Hàng: ");
        lblNgayTao = new JLabel("Ngày Tạo: ");

        pnlTop.add(lblMaHD);
        pnlTop.add(lblTenKH);
        pnlTop.add(lblNgayTao);
        add(pnlTop, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG CHI TIẾT SẢN PHẨM ---
        String[] columns = { "Mã SP", "Tên SP", "Đơn Giá", "Số Lượng", "Thành Tiền" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblChiTiet = new JTable(tableModel);
        tblChiTiet.setRowHeight(25);
        add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        // --- PHẦN 3: TỔNG TIỀN & NÚT ĐÓNG ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(Color.RED);
        pnlBottom.add(lblTongTien, BorderLayout.WEST);

        btnDong = new JButton("Đóng");
        btnDong.setBackground(new Color(108, 117, 125));
        btnDong.setForeground(Color.WHITE);
        btnDong.setPreferredSize(new Dimension(80, 30));
        pnlBottom.add(btnDong, BorderLayout.EAST);

        add(pnlBottom, BorderLayout.SOUTH);

        btnDong.addActionListener(e -> dispose());
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JLabel getLblMaHD() {
        return lblMaHD;
    }

    public JLabel getLblTenKH() {
        return lblTenKH;
    }

    public JLabel getLblNgayTao() {
        return lblNgayTao;
    }

    public JLabel getLblTongTien() {
        return lblTongTien;
    }
}
