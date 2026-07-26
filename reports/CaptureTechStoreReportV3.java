import com.techstore.controller.HoaDonController;
import com.techstore.controller.LoginController;
import com.techstore.controller.NhapHangController;
import com.techstore.controller.SanPhamController;
import com.techstore.controller.ThongKeController;
import com.techstore.model.ChiTietHoaDon;
import com.techstore.model.HoaDon;
import com.techstore.model.SanPham;
import com.techstore.service.HoaDonService;
import com.techstore.service.SanPhamService;
import com.techstore.view.ChiTietHoaDonDialog;
import com.techstore.view.ChonSanPhamDialog;
import com.techstore.view.HoaDonDialog;
import com.techstore.view.HoaDonFrame;
import com.techstore.view.HoaDonLienQuanDialog;
import com.techstore.view.LoginFrame;
import com.techstore.view.MainFrame;
import com.techstore.view.NhapHangFrame;
import com.techstore.view.SanPhamDialog;
import com.techstore.view.SanPhamFrame;
import com.techstore.view.ThongKeFrame;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaptureTechStoreReportV3 {
    private static final File SCREEN_DIR = new File("D:/webapp/OopBTL/reports/techstore_report_v3_screens");
    private static final File MYSQL_DIR = new File("D:/webapp/OopBTL/reports/techstore_report_v3_mysql");
    private static final String URL = "jdbc:mysql://localhost:3306/techstore_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final double UI_SCALE = 2.75;

    public static void main(String[] args) throws Exception {
        SCREEN_DIR.mkdirs();
        MYSQL_DIR.mkdirs();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        captureSwingScreens();
        captureMysqlTables();
        System.exit(0);
    }

    private static void captureSwingScreens() throws Exception {
        LoginFrame login = new LoginFrame();
        new LoginController(login);
        showFrame(login);
        saveComponent(login, new File(SCREEN_DIR, "01_dang_nhap.png"));
        SwingUtilities.invokeAndWait(() -> {
            login.getTxtUsername().setText("admin");
            login.getTxtPassword().setText("123456");
            login.getBtnLogin().doClick();
        });
        MainFrame main = waitForFrame(MainFrame.class, 8000);
        if (main == null) {
            throw new IllegalStateException("Không đăng nhập được bằng admin/123456.");
        }
        saveComponent(main, new File(SCREEN_DIR, "02_menu_chinh.png"));
        close(main);

        List<SanPham> products = new SanPhamService().layDanhSachSanPham();
        if (products.isEmpty()) {
            throw new IllegalStateException("Bảng san_pham không có dữ liệu thật để chụp.");
        }
        SanPham product = products.get(0);

        SanPhamFrame spFrame = new SanPhamFrame();
        new SanPhamController(spFrame);
        showFrame(spFrame);
        saveComponent(spFrame, new File(SCREEN_DIR, "03_san_pham_danh_sach.png"));
        captureProductDialogs(spFrame, product);
        close(spFrame);

        HoaDonFrame hdFrame = new HoaDonFrame();
        new HoaDonController(hdFrame);
        showFrame(hdFrame);
        saveComponent(hdFrame, new File(SCREEN_DIR, "08_hoa_don_danh_sach.png"));
        captureInvoiceDialogs(hdFrame, product);
        close(hdFrame);

        NhapHangFrame nhFrame = new NhapHangFrame();
        new NhapHangController(nhFrame);
        showFrame(nhFrame);
        fillImportRows(nhFrame, products);
        saveComponent(nhFrame, new File(SCREEN_DIR, "13_nhap_hang_import.png"));
        JOptionPane confirm = new JOptionPane("Bạn có chắc chắn muốn lưu danh sách sản phẩm này vào CSDL và cập nhật tồn kho kho hàng?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        saveDialog(confirm.createDialog(nhFrame, "Xác nhận lưu CSDL"), new File(SCREEN_DIR, "14_nhap_hang_xac_nhan.png"));
        close(nhFrame);

        ThongKeFrame tkFrame = new ThongKeFrame();
        new ThongKeController(tkFrame);
        showFrame(tkFrame);
        saveComponent(tkFrame, new File(SCREEN_DIR, "15_thong_ke_doanh_thu.png"));
        captureRelatedInvoices(tkFrame);
        close(tkFrame);
    }

    private static void captureProductDialogs(JFrame owner, SanPham product) throws Exception {
        SanPhamDialog add = new SanPhamDialog(owner, "Thêm Sản Phẩm Mới");
        add.getTxtMaSp().setText("Nhập mã sản phẩm mới");
        add.getTxtTenSp().setText("Nhập tên sản phẩm");
        add.getTxtHangSx().setText("Nhập hãng sản xuất");
        add.getTxtGiaBan().setText("0");
        add.getTxtSoLuong().setText("0");
        saveDialog(add, new File(SCREEN_DIR, "04_san_pham_them_moi.png"));

        SanPhamDialog edit = new SanPhamDialog(owner, "Sửa Thông Tin Sản Phẩm");
        edit.getTxtMaSp().setText(product.getMaSanPham());
        edit.getTxtMaSp().setEditable(false);
        edit.getTxtTenSp().setText(product.getTenSanPham());
        edit.getTxtHangSx().setText(product.getHangSanXuat());
        edit.getTxtGiaBan().setText(String.format("%.0f", product.getGiaBan()));
        edit.getTxtSoLuong().setText(String.valueOf(product.getSoLuongTon()));
        edit.getCboLoaiSp().setSelectedItem(displayType(product));
        edit.getCboLoaiSp().setEnabled(false);
        saveDialog(edit, new File(SCREEN_DIR, "05_san_pham_sua.png"));

        JOptionPane delete = new JOptionPane(
                "Bạn có chắc chắn muốn xóa sản phẩm:\n- Mã SP: " + product.getMaSanPham() + "\n- Tên SP: " + product.getTenSanPham() + "?\n\nLưu ý: Hành động này không thể hoàn tác!",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        saveDialog(delete.createDialog(owner, "Xác nhận xóa sản phẩm"), new File(SCREEN_DIR, "06_san_pham_xoa.png"));

        SanPhamFrame popupFrame = new SanPhamFrame();
        new SanPhamController(popupFrame);
        showFrame(popupFrame);
        selectFirstRow(popupFrame.getTblSanPham());
        SwingUtilities.invokeAndWait(() -> popupFrame.getPopupMenu().show(popupFrame.getTblSanPham(), popupFrame.getWidth() - 170, 95));
        saveComponent(popupFrame, new File(SCREEN_DIR, "07_san_pham_menu_thao_tac.png"));
        close(popupFrame);
    }

    private static void captureInvoiceDialogs(JFrame owner, SanPham product) throws Exception {
        HoaDonDialog invoice = new HoaDonDialog(owner);
        invoice.getTxtMaHD().setText("HD_MOI");
        invoice.getTxtTenKH().setText("Nhập tên khách hàng");
        double amount = product.getGiaBan();
        invoice.getModelGioHang().addRow(new Object[]{product.getMaSanPham(), product.getTenSanPham(), String.format("%,.0f", product.getGiaBan()), 1, String.format("%,.0f", amount)});
        invoice.getLblTongTien().setText("Tổng tiền: " + String.format("%,.0f", amount) + " VNĐ");
        saveDialog(invoice, new File(SCREEN_DIR, "09_hoa_don_tao_moi.png"));

        ChonSanPhamDialog choose = new ChonSanPhamDialog(invoice);
        choose.getTxtTimKiem().setText(product.getMaSanPham());
        DefaultTableModel model = choose.getTableModel();
        for (SanPham sp : new SanPhamService().layDanhSachSanPham()) {
            model.addRow(new Object[]{sp.getMaSanPham(), sp.getTenSanPham(), displayType(sp), String.format("%,.0f", sp.getGiaBan()), sp.getSoLuongTon()});
        }
        selectFirstRow(choose.getTblSanPham());
        saveDialog(choose, new File(SCREEN_DIR, "10_hoa_don_chon_san_pham.png"));

        HoaDon hd = firstInvoice();
        if (hd != null) {
            ChiTietHoaDonDialog detail = new ChiTietHoaDonDialog(owner);
            detail.getLblMaHD().setText("Mã HĐ: " + hd.getMaHoaDon());
            detail.getLblTenKH().setText("Khách Hàng: " + hd.getTenKhachHang());
            detail.getLblNgayTao().setText("Ngày Tạo: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(hd.getNgayTao()));
            double total = 0;
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                double line = ct.getDonGia() * ct.getSoLuong();
                total += line;
                detail.getTableModel().addRow(new Object[]{ct.getMaSanPham(), productName(ct.getMaSanPham()), String.format("%,.0f", ct.getDonGia()), ct.getSoLuong(), String.format("%,.0f", line)});
            }
            detail.getLblTongTien().setText("Tổng tiền: " + String.format("%,.0f", total) + " VNĐ");
            saveDialog(detail, new File(SCREEN_DIR, "11_hoa_don_chi_tiet.png"));
        }

        HoaDonFrame popupFrame = new HoaDonFrame();
        new HoaDonController(popupFrame);
        showFrame(popupFrame);
        selectFirstRow(popupFrame.getTblHoaDon());
        SwingUtilities.invokeAndWait(() -> popupFrame.getPopupMenu().show(popupFrame.getTblHoaDon(), popupFrame.getWidth() - 130, 90));
        saveComponent(popupFrame, new File(SCREEN_DIR, "12_hoa_don_menu_thao_tac.png"));
        close(popupFrame);
    }

    private static void captureRelatedInvoices(JFrame owner) throws Exception {
        String[] stat = firstSoldProduct();
        if (stat == null) {
            return;
        }
        HoaDonLienQuanDialog dialog = new HoaDonLienQuanDialog(owner);
        dialog.getLblTenSP().setText("Sản Phẩm: [" + stat[0] + "] - " + stat[1]);
        dialog.getLblTongSl().setText("Tổng số lượng bán: " + stat[2] + " cái");
        dialog.getLblTongTien().setText("Tổng tiền thu về: " + stat[3] + " VNĐ");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT hd.ma_hd, hd.ten_kh, hd.ngay_tao, ct.so_luong, ct.don_gia, ct.so_luong * ct.don_gia thanh_tien FROM hoa_don hd JOIN chi_tiet_hoa_don ct ON hd.ma_hd=ct.ma_hd WHERE ct.ma_sp='" + stat[0] + "' LIMIT 10")) {
            while (rs.next()) {
                dialog.getTableModel().addRow(new Object[]{
                        rs.getString("ma_hd"),
                        rs.getString("ten_kh"),
                        rs.getString("ngay_tao"),
                        rs.getInt("so_luong"),
                        String.format("%,.0f", rs.getDouble("don_gia")),
                        String.format("%,.0f", rs.getDouble("thanh_tien"))
                });
            }
        }
        saveDialog(dialog, new File(SCREEN_DIR, "16_thong_ke_hoa_don_lien_quan.png"));
    }

    private static void captureMysqlTables() throws Exception {
        saveQuery("01_mysql_danh_sach_bang.png", "MySQL Workbench - techstore_db: danh sách bảng",
                "SELECT table_name AS bang, table_rows AS so_dong FROM information_schema.tables WHERE table_schema='techstore_db' ORDER BY table_name", 20);
        saveQuery("02_mysql_user.png", "MySQL Workbench - bảng user",
                "SELECT ten_dang_nhap, vai_tro FROM user LIMIT 12", 12);
        saveQuery("03_mysql_san_pham.png", "MySQL Workbench - bảng san_pham",
                "SELECT ma_sp, ten_sp, hang_sx, FORMAT(gia_ban,0) gia_ban, so_luong, loai_sp FROM san_pham ORDER BY ma_sp LIMIT 14", 14);
        saveQuery("04_mysql_hoa_don.png", "MySQL Workbench - bảng hoa_don",
                "SELECT ma_hd, ten_kh, ngay_tao FROM hoa_don ORDER BY ngay_tao DESC LIMIT 14", 14);
        saveQuery("05_mysql_chi_tiet_hoa_don.png", "MySQL Workbench - bảng chi_tiet_hoa_don",
                "SELECT ma_hd, ma_sp, so_luong, FORMAT(don_gia,0) don_gia FROM chi_tiet_hoa_don ORDER BY ma_hd, ma_sp LIMIT 18", 18);
        saveQuery("06_mysql_phieu_nhap.png", "MySQL Workbench - bảng phieu_nhap",
                "SELECT ma_phieu_nhap, ngay_nhap, nguoi_nhap FROM phieu_nhap ORDER BY ngay_nhap DESC LIMIT 14", 14);
        saveQuery("07_mysql_chi_tiet_phieu_nhap.png", "MySQL Workbench - bảng chi_tiet_phieu_nhap",
                "SELECT ma_phieu_nhap, ma_sp, so_luong_nhap FROM chi_tiet_phieu_nhap ORDER BY ma_phieu_nhap, ma_sp LIMIT 18", 18);
    }

    private static void saveQuery(String fileName, String title, String sql, int maxRows) throws Exception {
        List<String> headers = new ArrayList<>();
        List<String[]> rows = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                headers.add(md.getColumnLabel(i));
            }
            while (rs.next() && rows.size() < maxRows) {
                String[] row = new String[headers.size()];
                for (int i = 0; i < headers.size(); i++) {
                    row[i] = rs.getString(i + 1);
                    if (row[i] == null) row[i] = "";
                }
                rows.add(row);
            }
        }
        drawMysqlImage(new File(MYSQL_DIR, fileName), title, headers, rows);
    }

    private static void drawMysqlImage(File out, String title, List<String> headers, List<String[]> rows) throws Exception {
        int scale = 2;
        Font titleFont = new Font("Arial", Font.BOLD, 26 * scale);
        Font mono = new Font("Consolas", Font.PLAIN, 17 * scale);
        Font monoBold = new Font("Consolas", Font.BOLD, 17 * scale);
        BufferedImage measure = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mg = measure.createGraphics();
        mg.setFont(mono);
        FontMetrics fm = mg.getFontMetrics();
        int[] widths = new int[headers.size()];
        for (int c = 0; c < headers.size(); c++) {
            widths[c] = Math.max(130 * scale, fm.stringWidth(headers.get(c)) + 34 * scale);
            for (String[] row : rows) {
                String v = trim(row[c], 42);
                widths[c] = Math.max(widths[c], fm.stringWidth(v) + 34 * scale);
            }
            widths[c] = Math.min(widths[c], 520 * scale);
        }
        mg.dispose();

        int margin = 28 * scale;
        int top = 72 * scale;
        int rowH = 40 * scale;
        int headH = 42 * scale;
        int tableW = 0;
        for (int w : widths) tableW += w;
        int w = tableW + margin * 2;
        int h = top + headH + Math.max(rows.size(), 1) * rowH + margin;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(245, 247, 250));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(43, 92, 145));
        g.fillRect(0, 0, w, top);
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString(title, margin, 47 * scale);
        int x = margin;
        int y = top + margin / 2;
        g.setFont(monoBold);
        for (int c = 0; c < headers.size(); c++) {
            g.setColor(new Color(227, 235, 246));
            g.fillRect(x, y, widths[c], headH);
            g.setColor(new Color(176, 190, 207));
            g.drawRect(x, y, widths[c], headH);
            g.setColor(new Color(17, 24, 39));
            g.drawString(headers.get(c), x + 12 * scale, y + 27 * scale);
            x += widths[c];
        }
        y += headH;
        g.setFont(mono);
        for (int r = 0; r < rows.size(); r++) {
            x = margin;
            for (int c = 0; c < headers.size(); c++) {
                g.setColor(r % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                g.fillRect(x, y, widths[c], rowH);
                g.setColor(new Color(216, 223, 233));
                g.drawRect(x, y, widths[c], rowH);
                g.setColor(new Color(17, 24, 39));
                g.drawString(trim(rows.get(r)[c], 42), x + 12 * scale, y + 26 * scale);
                x += widths[c];
            }
            y += rowH;
        }
        if (rows.isEmpty()) {
            g.setColor(new Color(107, 114, 128));
            g.drawString("Không có dữ liệu", margin, y + 26 * scale);
        }
        g.dispose();
        ImageIO.write(img, "png", out);
    }

    private static String trim(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }

    private static void fillImportRows(NhapHangFrame frame, List<SanPham> products) {
        frame.getTableModel().setRowCount(0);
        for (int i = 0; i < Math.min(3, products.size()); i++) {
            SanPham sp = products.get(i);
            frame.getTableModel().addRow(new Object[]{sp.getMaSanPham(), sp.getTenSanPham(), 5 + i});
        }
    }

    private static HoaDon firstInvoice() {
        HoaDonService service = new HoaDonService();
        List<HoaDon> invoices = service.layDanhSachHoaDon();
        if (invoices.isEmpty()) return null;
        return service.layChiTietHoaDon(invoices.get(0).getMaHoaDon());
    }

    private static String[] firstSoldProduct() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT sp.ma_sp, sp.ten_sp, SUM(ct.so_luong) sl, FORMAT(SUM(ct.so_luong*ct.don_gia),0) tien FROM san_pham sp JOIN chi_tiet_hoa_don ct ON sp.ma_sp=ct.ma_sp GROUP BY sp.ma_sp, sp.ten_sp ORDER BY SUM(ct.so_luong*ct.don_gia) DESC LIMIT 1")) {
            if (rs.next()) {
                return new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)};
            }
        }
        return null;
    }

    private static String productName(String maSp) {
        for (SanPham sp : new SanPhamService().layDanhSachSanPham()) {
            if (sp.getMaSanPham().equals(maSp)) return sp.getTenSanPham();
        }
        return maSp;
    }

    private static String displayType(SanPham sp) {
        return switch (sp.getClass().getSimpleName()) {
            case "LinhKienPC" -> "Linh Kiện PC";
            case "LinhKienDienThoai" -> "Điện Thoại";
            case "PhuKien" -> "Phụ Kiện";
            default -> sp.getClass().getSimpleName();
        };
    }

    private static void showFrame(JFrame frame) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame.setVisible(true);
            frame.toFront();
        });
        waitFor(() -> frame.isShowing() && frame.getWidth() > 0, 5000);
    }

    private static void saveDialog(JDialog dialog, File out) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            dialog.setModal(false);
            dialog.setVisible(true);
            dialog.toFront();
        });
        waitFor(() -> dialog.isShowing() && dialog.getWidth() > 0, 5000);
        saveComponent(dialog, out);
        SwingUtilities.invokeAndWait(dialog::dispose);
    }

    private static void saveComponent(Component component, File out) throws Exception {
        int w = Math.max(1, component.getWidth());
        int h = Math.max(1, component.getHeight());
        BufferedImage img = new BufferedImage((int) Math.round(w * UI_SCALE), (int) Math.round(h * UI_SCALE), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(UI_SCALE, UI_SCALE);
        component.paintAll(g);
        g.dispose();
        ImageIO.write(img, "png", out);
    }

    private static void selectFirstRow(JTable table) {
        if (table.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }

    private static <T extends JFrame> T waitForFrame(Class<T> type, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            for (Frame frame : Frame.getFrames()) {
                if (type.isInstance(frame) && frame.isShowing()) return type.cast(frame);
            }
            sleep(100);
        }
        return null;
    }

    private static void close(JFrame frame) throws Exception {
        SwingUtilities.invokeAndWait(frame::dispose);
        sleep(150);
    }

    private static void waitFor(Check check, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (check.ok()) return;
            sleep(100);
        }
        throw new IllegalStateException("Timeout khi chụp ảnh");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    private interface Check {
        boolean ok();
    }
}
