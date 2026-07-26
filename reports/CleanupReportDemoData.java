import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CleanupReportDemoData {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/techstore_db", "root", "1234");
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM chi_tiet_hoa_don WHERE ma_hd LIKE '%DEMO_REPORT%' OR ma_sp LIKE '%DEMO_REPORT%'");
            st.executeUpdate("DELETE FROM hoa_don WHERE ma_hd LIKE '%DEMO_REPORT%'");
            st.executeUpdate("DELETE FROM chi_tiet_phieu_nhap WHERE ma_phieu_nhap LIKE '%DEMO_REPORT%' OR ma_sp LIKE '%DEMO_REPORT%'");
            st.executeUpdate("DELETE FROM phieu_nhap WHERE ma_phieu_nhap LIKE '%DEMO_REPORT%'");
            st.executeUpdate("DELETE FROM san_pham WHERE ma_sp LIKE '%DEMO_REPORT%'");
        }
        System.out.println("Cleaned report demo data.");
    }
}
