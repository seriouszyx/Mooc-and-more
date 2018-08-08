package me.seriouszyx.servlet;

import me.seriouszyx.domain.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "InitServlet")
public class InitServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        // 创建一个 List 集合，用于保存用户注册信息
        List<User> list = new ArrayList<>();
        this.getServletContext().setAttribute("list", list);
    }



}
