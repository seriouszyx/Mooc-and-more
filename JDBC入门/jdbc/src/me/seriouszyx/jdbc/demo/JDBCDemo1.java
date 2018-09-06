package me.seriouszyx.jdbc.demo;

import com.mysql.cj.jdbc.Driver;
import org.junit.Test;

import java.sql.*;

public class JDBCDemo1 {

    /** jdbc 入门程序 */
    @Test
    public void demo1() {

        try {
            /** 加载驱动 */
            DriverManager.registerDriver(new Driver());

            /** 获得连接 */
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbctest?serverTimezone=GMT&useSSL=false", "root", "zhaoyixiang123");

            /** 创建SQL语句的对象 */
            String sql = "select * from user";
            Statement statement = conn.createStatement();

            /**并且执行SQL语句 */
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int uid = resultSet.getInt("uid");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");

                System.out.println(uid + " " +username + " " + password + " " + name);
            }

            /** 释放资源 */
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
