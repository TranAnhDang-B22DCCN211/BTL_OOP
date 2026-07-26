package com.techstore.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JButton btnSanPham, btnHoaDon, btnNhapHang, btnThongKe;

    public MainFrame() {
        setTitle("Hệ Thống Quản Lý Cửa Hàng");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("MENU CHÍNH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(4, 1, 15, 15));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(0, 50, 40, 50));

        btnSanPham = new JButton("Quản Lý Sản Phẩm");
        btnHoaDon = new JButton("Quản Lý Hóa Đơn");
        btnNhapHang = new JButton("Nhập Hàng (Import File)");
        btnThongKe = new JButton("Thống Kê Doanh Thu");

        Font btnFont = new Font("Arial", Font.BOLD, 14);
        btnSanPham.setFont(btnFont);
        btnHoaDon.setFont(btnFont);
        btnNhapHang.setFont(btnFont);
        btnThongKe.setFont(btnFont);

        panelButtons.add(btnSanPham);
        panelButtons.add(btnHoaDon);
        panelButtons.add(btnNhapHang);
        panelButtons.add(btnThongKe);

        add(panelButtons, BorderLayout.CENTER);
    }

    public JButton getBtnSanPham() {
        return btnSanPham;
    }

    public JButton getBtnHoaDon() {
        return btnHoaDon;
    }

    public JButton getBtnNhapHang() {
        return btnNhapHang;
    }

    public JButton getBtnThongKe() {
        return btnThongKe;
    }
}
