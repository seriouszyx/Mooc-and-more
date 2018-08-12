package me.seriouszyx.cart.servlet;

import me.seriouszyx.cart.data.LocalCache;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


/**
 *  收藏的控制器
 * */
@WebServlet(name = "FavoriteServlet")
public class FavoriteServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.equals(req.getServletPath(), "/favorite/favorite.do")) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                LocalCache.addFavorite(LocalCache.getProduct(Long.valueOf(productId)));
            }
            resp.sendRedirect("/favorite/list.do");
        } else if (Objects.equals(req.getServletPath(), "/favorite/list.do")) {
            req.setAttribute("products", LocalCache.getFavorite());
            req.getRequestDispatcher("/WEB-INF/views/biz/favorite.jsp").forward(req, resp);
        } else if (Objects.equals(req.getServletPath(), "/favorite/delete.do")) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                LocalCache.delFavorite(LocalCache.getProduct(Long.valueOf(productId)));
            }
            resp.sendRedirect("/favorite/list.do");
        }
    }
}
