package me.seriouszyx.jdbc.dao;

import me.seriouszyx.jdbc.bean.User;
import me.seriouszyx.jdbc.common.ConnectionUtil;

import javax.xml.transform.Result;
import java.sql.*;

/**
 * @ClassName UserDAO
 * @Description 用户DAO
 * @Author Yixiang Zhao
 * @Date 2018/9/7 21:18
 * @Version 1.0
 */
public class UserDAO {

    /**
     * @Author Yixiang Zhao
     * @Description 用户登录
     * @Date 21:28 2018/9/7
     * @Param [username, password]
     * @return me.seriouszyx.jdbc.bean.User 成功返回Bean，失败返回空
     **/
    public User login(String username, String password) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from user where username  = ? and password = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRealName(rs.getString("real_name"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            System.out.println("登录失败....");
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return user;
    }


    /**
     * @Author Yixiang Zhao
     * @Description 根据用户id查询用户信息
     * @Date 14:15 2018/9/8
     * @Param [id]
     * @return me.seriouszyx.jdbc.bean.User
     **/
    public User getUserById(Long id) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from user where id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRealName(rs.getString("real_name"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
            }
        } catch (Exception e) {
            System.out.println("查询用户信息失败....");
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return user;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 修改用户信息
     * @Date 14:33 2018/9/8
     * @Param [user]
     * @return boolean
     **/
    public boolean updateUser(User user) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "update user set username = ?, password = ?, real_name = ?," +
                "     birthday = ?, phone = ?, address = ? where id = ?";
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRealName());
            pstmt.setDate(4, new Date(user.getBirthday().getTime()));
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setLong(7, user.getId());
            pstmt.execute();
        } catch (Exception e) {
            System.out.println("查询用户信息失败....");
            e.printStackTrace();
            return false;
        } finally {
            ConnectionUtil.release(null, pstmt, conn);
        }

        return true;
    }
}
