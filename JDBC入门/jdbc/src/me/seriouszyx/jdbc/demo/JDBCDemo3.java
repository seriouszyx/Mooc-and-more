package me.seriouszyx.jdbc.demo;


import me.seriouszyx.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCDemo3 {
    /** 保存记录 */
    @Test
    public void save() {
        Connection conn = null;
        Statement statement = null;
        try {
            // 获得连接
            conn = JDBCUtils.getConnection();
            // 创建执行 SQL 语句的对象
            statement = conn.createStatement();
            // 编写 SQL
            String sql = "insert into user values (null, 'fff', '123', '小吴')";
            // 执行 SQL
            int num = statement.executeUpdate(sql);

            if (num > 0) {
                System.out.println("保存成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(statement, conn);
        }
    }
}
