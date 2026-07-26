package com.techstore.view;

import javax.swing.*;
import java.awt.*;

public class SanPhamDialog extends JDialog {
    private final JTextField txtMaSp;
    private final JTextField txtTenSp;
    private final JTextField txtHangSx;
    private final JTextField txtGiaBan;
    private final JTextField txtSoLuong;
    private final JComboBox<String> cboLoaiSp;
    private final JButton btnLuu;
    private final JButton btnHuy;

    public SanPhamDialog(Frame owner, String title) {
        super(owner, title, true);
        setSize(420, 360);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        formPanel.add(new JLabel("Mã Sản Phẩm:"));
        txtMaSp = new JTextField();
        formPanel.add(txtMaSp);

        formPanel.add(new JLabel("Tên Sản Phẩm:"));
        txtTenSp = new JTextField();
        formPanel.add(txtTenSp);

        formPanel.add(new JLabel("Hãng Sản Xuất:"));
        txtHangSx = new JTextField();
        formPanel.add(txtHangSx);

        formPanel.add(new JLabel("Giá Bán:"));
        txtGiaBan = new JTextField();
        formPanel.add(txtGiaBan);

        formPanel.add(new JLabel("Số Lượng:"));
        txtSoLuong = new JTextField();
        formPanel.add(txtSoLuong);

        formPanel.add(new JLabel("Loại Sản Phẩm:"));
        String[] loaiSp = { "Điện Thoại", "Linh Kiện PC", "Phụ Kiện" };
        cboLoaiSp = new JComboBox<>(loaiSp);
        formPanel.add(cboLoaiSp);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);

        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        add(buttonPanel, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dispose());
    }

    public JTextField getTxtMaSp() {
        return txtMaSp;
    }

    public JTextField getTxtTenSp() {
        return txtTenSp;
    }

    public JTextField getTxtHangSx() {
        return txtHangSx;
    }

    public JTextField getTxtGiaBan() {
        return txtGiaBan;
    }

    public JTextField getTxtSoLuong() {
        return txtSoLuong;
    }

    public JComboBox<String> getCboLoaiSp() {
        return cboLoaiSp;
    }

    public JButton getBtnLuu() {
        return btnLuu;
    }
}
