package me.seriouszyx.cart.servlet;

import me.seriouszyx.cart.data.LocalCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 *  浏览记录servlet
 * */
public class BrowseServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.equals(req.getServletPath(), "/browse/list.do")) {
            req.setAttribute("products", LocalCache.getBrowseLogs());
            req.getRequestDispatcher("/WEB-INF/views/biz/browse_list.jsp").forward(req, resp);
        } else if (Objects.equals(req.getServletPath(), "/browse/delete.do")) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                LocalCache.delBrowserLog(LocalCache.getProduct(Long.valueOf(productId)));
            }
            resp.sendRedirect("/browse/list.do");
        }
    }
}
