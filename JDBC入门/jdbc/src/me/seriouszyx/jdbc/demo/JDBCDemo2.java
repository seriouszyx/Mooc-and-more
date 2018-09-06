package me.seriouszyx.jdbc.demo;

import com.mysql.cj.jdbc.Driver;
import org.junit.Test;

import java.sql.*;

public class JDBCDemo2 {
    /** JDBC 资源的释放 */
    @Test
    public void demo1() {

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            /** 加载驱动 */
            DriverManager.registerDriver(new Driver());

            /** 获得连接 */
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbctest?serverTimezone=GMT&useSSL=false", "root", "zhaoyixiang123");

            /** 创建SQL语句的对象 */
            String sql = "select * from user";
            statement = conn.createStatement();

            /**并且执行SQL语句 */
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int uid = resultSet.getInt("uid");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");

                System.out.println(uid + " " +username + " " + password + " " + name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            /** 释放资源 */
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {

                }
                resultSet = null; // 垃圾回收机制更早回收对象
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {

                }
                statement = null;
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {

                }
                conn = null;
            }
        }
    }
}
