package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HoaDonFrame extends JFrame {
    private JTable tblHoaDon;
    private DefaultTableModel tableModel;
    private JButton btnThemMoi, btnQuayLai;
    private JPopupMenu popupMenu;
    private JMenuItem itemXem, itemXoa;

    public HoaDonFrame() {
        setTitle("Quản Lý Hóa Đơn");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnThemMoi = new JButton("Thêm Hóa Đơn Mới");
        btnThemMoi.setBackground(new Color(40, 167, 69));
        btnThemMoi.setForeground(Color.WHITE);
        topPanel.add(btnThemMoi);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = { "Mã HĐ", "Tên Khách Hàng", "Ngày Tạo", "Thao tác" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHoaDon = new JTable(tableModel);
        tblHoaDon.setRowHeight(30);
        add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnQuayLai = new JButton("⬅ Quay lại Menu");
        btnQuayLai.setBackground(new Color(108, 117, 125));
        btnQuayLai.setForeground(Color.WHITE);
        bottomPanel.add(btnQuayLai);
        add(bottomPanel, BorderLayout.SOUTH);

        popupMenu = new JPopupMenu();
        itemXem = new JMenuItem("Xem Chi Tiết Hóa Đơn");
        itemXoa = new JMenuItem("Xóa Hóa Đơn");
        popupMenu.add(itemXem);
        popupMenu.add(itemXoa);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTblHoaDon() {
        return tblHoaDon;
    }

    public JButton getBtnThemMoi() {
        return btnThemMoi;
    }

    public JButton getBtnQuayLai() {
        return btnQuayLai;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JMenuItem getItemXem() {
        return itemXem;
    }

    public JMenuItem getItemXoa() {
        return itemXoa;
    }
}
