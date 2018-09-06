package me.seriouszyx.jdbc.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.cj.jdbc.Driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName JDBCUtils2
 * @Description 连接池工具类
 * @Author Yixiang Zhao
 * @Date 2018/9/6 17:21
 * @Version 1.0
 */
public class JDBCUtils2 {

    private static final ComboPooledDataSource dataSource = new ComboPooledDataSource();

    /**
     *  获得连接的方法
     * */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     *  资源释放
     * */
    public static void release(Statement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            statement = null;
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }

    public static void release(ResultSet resultSet, Statement statement, Connection connection) {
        release(statement, connection);
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resultSet = null;
        }
    }
}
