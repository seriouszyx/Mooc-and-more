package me.seriouszyx.servlet;

import me.seriouszyx.dao.UsersDAO;
import me.seriouszyx.mybatis.Users;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/updateusers")
public class UsersUpdateServlet extends HttpServlet {

    private UsersDAO usersDAO = new UsersDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String nickname = request.getParameter("nickname");
        String age = request.getParameter("age");
        String gender = request.getParameter("gender");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String remark = request.getParameter("remark");

        Users user = new Users(Integer.parseInt(id), nickname, Integer.parseInt(age), gender, phone, email, new Date(), remark);
        usersDAO.updateUsers(user);
        response.sendRedirect("/detail?id="+user.getId());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
