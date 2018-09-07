package me.seriouszyx.jdbc.servlet;

import me.seriouszyx.jdbc.bean.Message;
import me.seriouszyx.jdbc.service.MessageService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *  消息列表的servlet
 * */
@WebServlet(name = "MessageListServlet")
public class MessageListServlet extends HttpServlet {
    private MessageService messageService;

    @Override
    public void init() throws ServletException {
        super.init();
        messageService = new MessageService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取当前页码
        String pageStr = request.getParameter("page");
        int page = 1; // 页码默认值是1
        if (pageStr != null && (!"".equals(pageStr))) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        List<Message> messages = messageService.getMessages(page, 5); // 分页查询全部留言
        int count = messageService.countMessage();
        int last = count % 5 == 0 ? count / 5 : count / 5 + 1;

        request.setAttribute("messages", messages);
        request.setAttribute("page",page);
        request.setAttribute("last", last);
        request.getRequestDispatcher("/WEB-INF/views/biz/message_list.jsp").forward(request, response);

    }

    @Override
    public void destroy() {
        super.destroy();
        messageService = null;
    }
}
