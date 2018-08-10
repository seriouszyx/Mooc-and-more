package me.seriouszyx.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "SessionFilter")
public class SessionFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String loginUser = (String) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            // 没有登陆
            response.sendRedirect(request.getContextPath() + "/index.jsp?flag=1");
            return ;
        } else {
            chain.doFilter(request, response);
            return ;
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
