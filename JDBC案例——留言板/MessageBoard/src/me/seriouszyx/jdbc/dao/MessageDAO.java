package me.seriouszyx.jdbc.dao;

import me.seriouszyx.jdbc.bean.Message;
import me.seriouszyx.jdbc.common.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  消息DAO
 */
public class MessageDAO {
    
    /**
     * @Author Yixiang Zhao
     * @Description //分页查询全部留言
     * @Date 20:02 2018/9/7
     * @Param [page, pageSize]【当前页码，每页记录数】
     * @return java.util.List<me.seriouszyx.jdbc.bean.Message>
     **/
    public List<Message> getMessages(int page, int pageSize) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from message order by create_time desc limit ?, ?"; // limit m, n 从第m条开始，取出n条数据
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("create_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return messages;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 计算所有留言数量
     * @Date 20:11 2018/9/7
     * @Param []
     * @return int
     **/
    public int countMessages() {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select count(*) total from message";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return 0;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 保存留言信息
     * @Date 15:41 2018/9/8
     * @Param [message]
     * @return boolean
     **/
    public boolean save(Message message) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "insert into message (user_id, username, title, content, create_time) values(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, message.getUserId());
            pstmt.setString(2, message.getUsername());
            pstmt.setString(3, message.getTitle());
            pstmt.setString(4, message.getContent());
            pstmt.setTimestamp(5, new Timestamp(message.getCreateTime().getTime()));
            pstmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("保存留言信息失败....");
            return false;
        } finally {
            ConnectionUtil.release(null, pstmt, conn);
        }

        return true;
    }
}
