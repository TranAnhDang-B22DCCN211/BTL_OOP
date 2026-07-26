package com.techstore.controller;

import com.techstore.view.HoaDonFrame;
import com.techstore.view.MainFrame;
import com.techstore.view.SanPhamFrame;
import com.techstore.view.ThongKeFrame;

public class MainController {
    private final MainFrame view;

    public MainController(MainFrame view) {
        this.view = view;
        initController();
    }

    private void initController() {
        view.getBtnSanPham().addActionListener(e -> {
            view.dispose();
            SanPhamFrame sanPhamFrame = new SanPhamFrame();
            new SanPhamController(sanPhamFrame);
            sanPhamFrame.setVisible(true);
        });

        view.getBtnHoaDon().addActionListener(e -> {
            view.dispose();
            HoaDonFrame hdFrame = new HoaDonFrame();
            new HoaDonController(hdFrame);
            hdFrame.setVisible(true);
        });

        view.getBtnThongKe().addActionListener(e -> {
            view.dispose();
            ThongKeFrame tkFrame = new ThongKeFrame();
            new ThongKeController(tkFrame);
            tkFrame.setVisible(true);
        });
    }
}
