package me.seriouszyx.jdbc.demo;

import me.seriouszyx.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCDemo4 {

    /** 保存数据 */
    @Test
    public void save() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 获得连接
            conn = JDBCUtils.getConnection();
            // 编写 SQL
            String sql = "insert into user values (null, ?, ?, ?)";
            // 预处理 SQL
            pstmt = conn.prepareStatement(sql);
            // 设置参数的值
            pstmt.setString(1, "qqq");
            pstmt.setString(2, "123");
            pstmt.setString(3, "张武");
            // 执行 SQL
            int num = pstmt.executeUpdate();
            if (num > 0) {
                System.out.println("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(pstmt, conn);
        }
    }

    /** 修改数据 */
    @Test
    public void update() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "update user set username = ?, password = ?, name = ? where uid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "www");
            pstmt.setString(2, "321");
            pstmt.setString(3, "张六");
            pstmt.setInt(4, 6);
            int num = pstmt.executeUpdate();
            if (num > 0) {
                System.out.println("修改成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(pstmt, conn);
        }
    }

    /** 删除数据 */
    @Test
    public void delete() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "delete from user where uid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 1);
            int num = pstmt.executeUpdate();
            if (num > 0) {
                System.out.println("刪除成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(pstmt, conn);
        }
    }

    /** 查询所有数据 */
    @Test
    public void findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "select * from user";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("uid")+"  "+rs.getString("username")+"  "+rs.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(rs, pstmt, conn);
        }
    }

    /** 查询一条数据 */
    @Test
    public void findOne() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "select * from user where uid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 2);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("uid")+"  "+rs.getString("username")+"  "+rs.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(rs, pstmt, conn);
        }
    }
}
