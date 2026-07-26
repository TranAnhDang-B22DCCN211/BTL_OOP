package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HoaDonDialog extends JDialog {
    private JTextField txtMaHD, txtTenKH;
    private JSpinner spinNgayTao;
    private JButton btnThemSanPham, btnXoaKhoiGio, btnLuu, btnHuy;
    private JTable tblGioHang;
    private DefaultTableModel modelGioHang;
    private JLabel lblTongTien;

    public HoaDonDialog(Frame owner) {
        super(owner, "Tạo Hóa Đơn Bán Hàng", true);
        setSize(750, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlKhachHang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlKhachHang.setBorder(BorderFactory.createTitledBorder("Thông tin Hóa Đơn"));

        pnlKhachHang.add(new JLabel("Mã HĐ:"));
        txtMaHD = new JTextField(8);
        txtMaHD.setText("HD001");
        txtMaHD.setEditable(false);
        txtMaHD.setBackground(new Color(240, 240, 240));
        pnlKhachHang.add(txtMaHD);

        pnlKhachHang.add(new JLabel("   Tên Khách Hàng:"));
        txtTenKH = new JTextField(15);
        pnlKhachHang.add(txtTenKH);

        pnlKhachHang.add(new JLabel("   Ngày tạo:"));
        spinNgayTao = new JSpinner(new SpinnerDateModel());
        spinNgayTao.setEditor(new JSpinner.DateEditor(spinNgayTao, "dd/MM/yyyy"));
        pnlKhachHang.add(spinNgayTao);

        add(pnlKhachHang, BorderLayout.NORTH);

        String[] cols = { "Mã SP", "Tên SP", "Đơn Giá", "Số Lượng", "Thành Tiền" };
        modelGioHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGioHang = new JTable(modelGioHang);
        tblGioHang.setRowHeight(25);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        pnlCenter.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);

        // Tạo dải nút bên dưới bảng (Chứa nút Thêm và Xóa nằm cạnh nhau ở giữa)
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnThemSanPham = new JButton("+ Thêm Sản Phẩm");
        btnThemSanPham.setBackground(new Color(0, 123, 255));
        btnThemSanPham.setForeground(Color.WHITE);
        btnThemSanPham.setFont(new Font("Arial", Font.BOLD, 14));
        btnThemSanPham.setPreferredSize(new Dimension(180, 35));

        btnXoaKhoiGio = new JButton("- Xóa Sản Phẩm");
        btnXoaKhoiGio.setBackground(new Color(220, 53, 69));
        btnXoaKhoiGio.setForeground(Color.WHITE);
        btnXoaKhoiGio.setFont(new Font("Arial", Font.BOLD, 14));
        btnXoaKhoiGio.setPreferredSize(new Dimension(180, 35));

        pnlActions.add(btnThemSanPham);
        pnlActions.add(btnXoaKhoiGio);

        pnlCenter.add(pnlActions, BorderLayout.SOUTH);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 18));
        lblTongTien.setForeground(Color.RED);
        pnlBottom.add(lblTongTien, BorderLayout.WEST);

        JPanel pnlThanhToan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Arial", Font.BOLD, 14));
        btnLuu.setPreferredSize(new Dimension(100, 35));

        btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(80, 35));

        pnlThanhToan.add(btnLuu);
        pnlThanhToan.add(btnHuy);
        pnlBottom.add(pnlThanhToan, BorderLayout.EAST);

        add(pnlBottom, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dispose());
    }

    public JTextField getTxtMaHD() {
        return txtMaHD;
    }

    public JTextField getTxtTenKH() {
        return txtTenKH;
    }

    public java.util.Date getNgayTao() {
        return (java.util.Date) spinNgayTao.getValue();
    }

    public JButton getBtnThemSanPham() {
        return btnThemSanPham;
    }

    public JButton getBtnXoaKhoiGio() {
        return btnXoaKhoiGio;
    }

    public JButton getBtnLuu() {
        return btnLuu;
    }

    public DefaultTableModel getModelGioHang() {
        return modelGioHang;
    }

    public JTable getTblGioHang() {
        return tblGioHang;
    }

    public JLabel getLblTongTien() {
        return lblTongTien;
    }
}
