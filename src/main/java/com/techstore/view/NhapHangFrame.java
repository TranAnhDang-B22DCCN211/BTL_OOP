package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NhapHangFrame extends JFrame {
    private JTable tblNhapHang;
    private DefaultTableModel tableModel;
    private JButton btnImport, btnLuuCSDL, btnQuayLai;

    public NhapHangFrame() {
        setTitle("Quản Lý Nhập Hàng (Import File)");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnImport = new JButton("Chọn File Nhập Hàng (.txt / .csv)");
        btnImport.setBackground(new Color(0, 123, 255));
        btnImport.setForeground(Color.WHITE);
        btnImport.setFont(new Font("Arial", Font.BOLD, 13));
        topPanel.add(btnImport);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = { "Mã Sản Phẩm", "Tên Sản Phẩm", "Số Lượng Nhập" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblNhapHang = new JTable(tableModel);
        tblNhapHang.setRowHeight(30);
        add(new JScrollPane(tblNhapHang), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        btnQuayLai = new JButton("⬅ Quay lại Menu");
        btnQuayLai.setBackground(new Color(108, 117, 125));
        btnQuayLai.setForeground(Color.WHITE);
        bottomPanel.add(btnQuayLai, BorderLayout.WEST);

        btnLuuCSDL = new JButton("💾 Lưu Vào CSDL");
        btnLuuCSDL.setBackground(new Color(40, 167, 69));
        btnLuuCSDL.setForeground(Color.WHITE);
        btnLuuCSDL.setFont(new Font("Arial", Font.BOLD, 13));
        bottomPanel.add(btnLuuCSDL, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public JButton getBtnImport() {
        return btnImport;
    }

    public JButton getBtnLuuCSDL() {
        return btnLuuCSDL;
    }

    public JButton getBtnQuayLai() {
        return btnQuayLai;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTblNhapHang() {
        return tblNhapHang;
    }
}
