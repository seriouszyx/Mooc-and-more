package me.seriouszyx.jdbc.filter;

import me.seriouszyx.jdbc.bean.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 *  用户Filter
 * */
@WebFilter(filterName = "UserFilter")
public class UserFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        User user = (User)((HttpServletRequest)servletRequest).getSession().getAttribute("user");
        servletRequest.setAttribute("user",user);
        chain.doFilter(servletRequest, servletResponse);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
