package me.seriouszyx.jdbc.common;

import com.mysql.jdbc.Driver;

import java.sql.*;

/**
 *  数据库操作的公共类
 * */
public final class ConnectionUtil {
    private static String url = "jdbc:mysql://localhost:3306/message_board?serverTimezone=GMT&useSSL=false";
    private static String user = "root";
    private static String password = "zhaoyixiang123";

    private ConnectionUtil() {};

    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new Driver());
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("创建数据库连接失败...");
            e.printStackTrace();
        }
        return null;
    }


    public static void release(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
