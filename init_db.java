import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class init_db {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true";
        String user = "root";
        String password = "root";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            
            System.out.println("1. 创建数据库vhr...");
            stmt.execute("CREATE DATABASE IF NOT EXISTS vhr CHARACTER SET utf8mb4;");
            stmt.execute("USE vhr;");
            
            System.out.println("2. 检查是否已存在数据表...");
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM information_schema.TABLES WHERE TABLE_SCHEMA='vhr'");
            rs.next();
            int tableCount = rs.getInt("cnt");
            System.out.println("   当前表数量: " + tableCount);
            
            if (tableCount < 30) {
                System.out.println("3. 开始执行vhr.sql...(这可能需要几分钟)");
                
                BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("vhr-project/vhr/vhr.sql"), StandardCharsets.UTF_8));
                
                StringBuilder sql = new StringBuilder();
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) continue;
                    sql.append(line).append(" ");
                    if (line.endsWith(";")) {
                        try {
                            stmt.execute(sql.toString());
                            count++;
                            if (count % 50 == 0) System.out.println("   已执行 " + count + " 条SQL...");
                        } catch (Exception e) {
                            // 忽略个别错误，继续执行
                        }
                        sql = new StringBuilder();
                    }
                }
                br.close();
                System.out.println("   vhr.sql 执行完成！共执行约" + count + "条SQL");
                
                System.out.println("4. 执行招聘管理模块recruitment.sql...");
                br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("vhr-project/vhr/recruitment.sql"), StandardCharsets.UTF_8));
                sql = new StringBuilder();
                count = 0;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) continue;
                    sql.append(line).append(" ");
                    if (line.endsWith(";")) {
                        try {
                            stmt.execute(sql.toString());
                            count++;
                        } catch (Exception e) {
                            System.out.println("   Warning: " + e.getMessage());
                        }
                        sql = new StringBuilder();
                    }
                }
                br.close();
                System.out.println("   recruitment.sql 执行完成！共执行" + count + "条SQL");
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM information_schema.TABLES WHERE TABLE_SCHEMA='vhr'");
            rs.next();
            System.out.println("\n5. 数据库初始化完成！当前共有 " + rs.getInt("cnt") + " 张数据表");
            
            rs = stmt.executeQuery("SELECT name FROM menu WHERE name LIKE '%招聘%' OR name LIKE '%简历%' OR name LIKE '%面试%' OR name LIKE '%Offer%'");
            System.out.println("\n6. 验证招聘管理菜单：");
            while (rs.next()) {
                System.out.println("   - " + rs.getString("name"));
            }
            
            rs = stmt.executeQuery("SHOW TABLES LIKE '%resume%' OR Tables_in_vhr LIKE '%interview%' OR Tables_in_vhr LIKE '%offer%'");
            System.out.println("\n7. 验证招聘管理数据表：");
            while (rs.next()) {
                System.out.println("   - " + rs.getString(1));
            }
            
            conn.close();
            System.out.println("\n✅ 数据库初始化成功！可以启动后端了！");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
