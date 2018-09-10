package me.seriouszyx.servlet;

import me.seriouszyx.dao.UsersDAO;
import me.seriouszyx.mybatis.Users;
import org.omg.PortableInterceptor.INACTIVE;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/deluser")
public class UsersDelServlet extends HttpServlet {

    private UsersDAO usersDAO = new UsersDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String type = request.getParameter("type");
        if ("lock".equals(type)) {
            // 锁定操作
            Users user = new Users();
            user.setId(Integer.parseInt(id));
            user.setUserStatus(1);
            usersDAO.updateUsers(user);
        } else if ("del".equals(type)) {
            // 删除操作
            usersDAO.delUsers(Integer.parseInt(id));
        } else if ("unlock".equals(type)) {
            // 解锁操作
            Users user = new Users();
            user.setId(Integer.parseInt(id));
            user.setUserStatus(0);
            usersDAO.updateUsers(user);
        }

        response.sendRedirect("/index");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
