package me.seriouszyx.jdbc.utils;

import com.mysql.cj.jdbc.Driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName JDBCUtils
 * @Description JDBC 工具类
 * @Author Yixiang Zhao
 * @Date 2018/9/6 15:43
 * @Version 1.0
 */
public class JDBCUtils {

    private static final String url;
    private static final String username;
    private static final String password;

    static {
        // 加载属性文件并解析
        Properties properties = new Properties();
        InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    /**
     *  注册驱动的方法
     **/
    public static void loadDriver() throws SQLException {
        DriverManager.registerDriver(new Driver());
    }

    /**
     *  获得连接的方法
     * */
    public static Connection getConnection() throws SQLException {
        loadDriver();
        return DriverManager.getConnection(url, username, password);
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
