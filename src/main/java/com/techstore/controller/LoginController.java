package com.techstore.controller;

import com.techstore.repository.UserRepository;
import com.techstore.view.LoginFrame;
import com.techstore.view.MainFrame;

import javax.swing.*;

public class LoginController {

    private final LoginFrame view;
    private final UserRepository repo;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.repo = new UserRepository();
        initController();
    }

    private void initController() {
        view.getBtnExit().addActionListener(e -> System.exit(0));

        view.getBtnLogin().addActionListener(e -> {
            String user = view.getTxtUsername().getText().trim();
            String pass = new String(view.getTxtPassword().getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            String role = repo.kiemTraDangNhap(user, pass);
            if (role != null) {
                view.dispose();
                MainFrame mainFrame = new MainFrame();
                new MainController(mainFrame);
                mainFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(view, "Sai tên đăng nhập hoặc mật khẩu!");
            }
        });
    }
}
