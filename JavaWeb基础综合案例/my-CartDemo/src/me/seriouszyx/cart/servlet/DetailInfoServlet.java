package me.seriouszyx.cart.servlet;


import me.seriouszyx.cart.data.LocalCache;
import me.seriouszyx.cart.data.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/** 详情 servlet */
public class DetailInfoServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.equals(req.getServletPath(), "/detail/detail.do")) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                Product product = LocalCache.getProduct(Long.valueOf(productId));
                req.setAttribute("product", product);
                LocalCache.addBrowserLog(product);
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/biz/detail.jsp").forward(req, resp);
    }
}
