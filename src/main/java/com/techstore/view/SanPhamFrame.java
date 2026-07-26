package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SanPhamFrame extends JFrame {
    private final JTable tblSanPham;
    private final DefaultTableModel tableModel;
    private JButton btnQuayLai;
    private final JPopupMenu popupMenu;
    private final JMenuItem itemSua;
    private final JMenuItem itemXoa;

    public SanPhamFrame() {
        setTitle("Quản Lý Sản Phẩm");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        add(topPanel, BorderLayout.NORTH);

        String[] columns = { "Mã SP", "Tên SP", "Hãng SX", "Giá Bán", "Số Lượng", "Loại", "Thao tác" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSanPham = new JTable(tableModel);
        tblSanPham.setRowHeight(30);
        add(new JScrollPane(tblSanPham), BorderLayout.CENTER);

        popupMenu = new JPopupMenu();
        itemSua = new JMenuItem("Sửa Sản Phẩm");
        itemXoa = new JMenuItem("Xóa Sản Phẩm");
        popupMenu.add(itemSua);
        popupMenu.add(itemXoa);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnQuayLai = new JButton("⬅ Quay lại Menu");
        btnQuayLai.setBackground(new Color(108, 117, 125));
        btnQuayLai.setForeground(Color.WHITE);
        bottomPanel.add(btnQuayLai);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTblSanPham() {
        return tblSanPham;
    }

    public JButton getBtnQuayLai() {
        return btnQuayLai;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JMenuItem getItemSua() {
        return itemSua;
    }

    public JMenuItem getItemXoa() {
        return itemXoa;
    }
}
