import java.sql.*;

public class FixPassword {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/vhr?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "root";
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        
        String bcryptPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
        
        stmt.execute("UPDATE hr SET password = '" + bcryptPassword + "' WHERE id = 1");
        
        ResultSet rs = stmt.executeQuery("SELECT id, username, name, password FROM hr");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("用户名: " + rs.getString("username"));
            System.out.println("姓名: " + rs.getString("name"));
            System.out.println("密码(前30位): " + rs.getString("password").substring(0, 30) + "...");
        }
        
        conn.close();
        System.out.println("\n✅ 密码已重置成功！");
        System.out.println("登录账号: admin / 123");
    }
}
