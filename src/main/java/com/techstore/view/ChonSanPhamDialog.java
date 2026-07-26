package com.techstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ChonSanPhamDialog extends JDialog {
    private JTextField txtTimKiem;
    private JComboBox<String> cboDanhMuc;
    private JButton btnTim, btnChon, btnQuayLai;
    private JTable tblSanPham;
    private DefaultTableModel tableModel;

    public ChonSanPhamDialog(JDialog owner) {
        super(owner, "Tìm Kiếm & Chọn Sản Phẩm", true);
        setSize(700, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- PHẦN 1: TÌM KIẾM & LỌC ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        pnlTop.add(new JLabel("Tìm tên SP:"));
        txtTimKiem = new JTextField(15);
        pnlTop.add(txtTimKiem);

        pnlTop.add(new JLabel("  Danh mục:"));
        String[] danhMuc = { "Tất cả", "Điện Thoại", "Linh Kiện PC", "Phụ Kiện" };
        cboDanhMuc = new JComboBox<>(danhMuc);
        pnlTop.add(cboDanhMuc);

        btnTim = new JButton("Tìm Kiếm");
        btnTim.setBackground(new Color(23, 162, 184));
        btnTim.setForeground(Color.WHITE);
        pnlTop.add(btnTim);

        add(pnlTop, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DANH SÁCH ---
        String[] columns = { "Mã SP", "Tên Sản Phẩm", "Loại", "Giá Bán", "Tồn Kho" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSanPham = new JTable(tableModel);
        tblSanPham.setRowHeight(25);
        add(new JScrollPane(tblSanPham), BorderLayout.CENTER);

        // --- PHẦN 3: NÚT CHỌN & QUAY LẠI ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        btnQuayLai = new JButton("⬅ Quay Lại");

        btnChon = new JButton("✔ Chọn Sản Phẩm Này");
        btnChon.setBackground(new Color(40, 167, 69));
        btnChon.setForeground(Color.WHITE);
        btnChon.setFont(new Font("Arial", Font.BOLD, 13));

        pnlBottom.add(btnQuayLai);
        pnlBottom.add(btnChon);
        add(pnlBottom, BorderLayout.SOUTH);

        btnQuayLai.addActionListener(e -> dispose());
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTblSanPham() {
        return tblSanPham;
    }

    public JButton getBtnChon() {
        return btnChon;
    }

    public JTextField getTxtTimKiem() {
        return txtTimKiem;
    }

    public JComboBox<String> getCboDanhMuc() {
        return cboDanhMuc;
    }

    public JButton getBtnTim() {
        return btnTim;
    }
}
