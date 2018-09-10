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

@WebServlet("/addusers")
public class UsersAddServlet extends HttpServlet {

    private UsersDAO usersDAO = new UsersDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String userpass = request.getParameter("userpass");
        String nickname = request.getParameter("nickname");
        String age = request.getParameter("age");
        String gender = request.getParameter("gender");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        Users user = new Users(username, userpass, nickname, Integer.parseInt(age), gender, phone, email, new Date(), new Date(), new Date(), 0);

        user = usersDAO.addUser(user);

        response.sendRedirect("/detail?id=" + user.getId());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
