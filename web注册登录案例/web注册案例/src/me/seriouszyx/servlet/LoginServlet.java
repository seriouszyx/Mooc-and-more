package me.seriouszyx.servlet;

import me.seriouszyx.domain.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        List<User> list = (List<User>) this.getServletContext().getAttribute("list");
        for (User user : list) {
            System.out.println(user);
            System.out.println(username);
            System.out.println(user.getUsername());
            if (username.equals(user.getUsername())) {
                // 用户名正确
                if (password.equals(user.getPassword())) {
                    // 密码正确，登陆成功

                    if (request.getParameter("remember").equals("true")) {
                        // 记住用户名
                        Cookie cookie = new Cookie("username", user.getUsername());
                        cookie.setMaxAge(60*60*24);
                        response.addCookie(cookie);
                    }

                    request.getSession().setAttribute("user",user );
                    response.sendRedirect("/success.jsp");
                    return ;
                }
            }
        }

        // 登录失败
        request.setAttribute("msg", "用户名或密码错误");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
