import java.sql.*;
public class CheckDemoGone {
 public static void main(String[] a) throws Exception {
  Class.forName("com.mysql.cj.jdbc.Driver");
  try(Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/techstore_db","root","1234"); Statement s=c.createStatement()){
   String[] qs={"SELECT COUNT(*) FROM san_pham WHERE ma_sp LIKE '%DEMO_REPORT%'","SELECT COUNT(*) FROM hoa_don WHERE ma_hd LIKE '%DEMO_REPORT%'","SELECT COUNT(*) FROM phieu_nhap WHERE ma_phieu_nhap LIKE '%DEMO_REPORT%'"};
   for(String q: qs){ try(ResultSet r=s.executeQuery(q)){ r.next(); System.out.println(r.getInt(1)); }}
  }
 }
}
