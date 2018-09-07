#   JDBC 案例——留言板

##  需求分析

![TIM截图20180906203135.png](https://i.loli.net/2018/09/07/5b925ac729499.png)

##  工程创建

先创建文件目录。

![TIM截图20180907190752.png](https://i.loli.net/2018/09/07/5b925c18557c3.png)

为了方便，前端静态页面在`资料`中给出，复制到相应的文件夹即可，这样可以将注意力集中到后端实现。

**注意将 `lib` 中的 jar 包导入工程。**

##  数据库操作工具类

为了简化数据库的连接，我们提前写好数据库连接的工具类。

```java
/**
 *  数据库操作的公共类
 * */
public final class ConnectionUtil {
    private static String url = "jdbc:mysql://localhost:3306/message_board?serverTimezone=GMT&useSSL=false";
    private static String user = "root";
    private static String password = "zhaoyixiang123";

    private ConnectionUtil() {};

    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new Driver());
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("创建数据库连接失败...");
            e.printStackTrace();
        }
        return null;
    }


    public static void release(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
```

然后在 `web.xml` 文件中配置启动页面及错误页面。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <welcome-file-list>
        <welcome-file>/index.jsp</welcome-file>
    </welcome-file-list>

    <error-page>
        <error-code>404</error-code>
        <location>/WEB-INF/views/error/404.jsp</location>
    </error-page>

    <error-page>
        <error-code>500</error-code>
        <location>/WEB-INF/views/error/500.jsp</location>
    </error-page>

</web-app>
```

启动服务器，进入启动页面 `index.jsp` 即成功。

##  查询及分页的处理

首先创建 `Message` 类用于封装 message 表中的数据。

```java
/**
 *  MessageBean
 */
public class Message {

    private long id;

    private long userId;

    private String username;

    private String title;

    private String content;

    private Date createTime;
    
    ...
}
```

创建 `MessageListServlet`，接受当前页数，并查询当前页数的所有信息。

```java
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
```

其中调用了 service 层的代码。

```java
/**
 *  消息Service
 */
public class MessageService {

    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public List<Message> getMessages(int page, int pageSize) {
        return messageDAO.getMessages(page, pageSize);
    }

    public int countMessage() {
        return messageDAO.countMessages();
    }
}
```

Service 层调用 DAO 层代码，完成数据库的查询。

```java
/**
 *  消息DAO
 */
public class MessageDAO {
    
    /**
     * @Author Yixiang Zhao
     * @Description //分页查询全部留言
     * @Date 20:02 2018/9/7
     * @Param [page, pageSize]【当前页码，每页记录数】
     * @return java.util.List<me.seriouszyx.jdbc.bean.Message>
     **/
    public List<Message> getMessages(int page, int pageSize) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from message order by create_time desc limit ?, ?"; // limit m, n 从第m条开始，取出n条数据
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("create_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return messages;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 计算所有留言数量
     * @Date 20:11 2018/9/7
     * @Param []
     * @return int
     **/
    public int countMessages() {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select count(*) total from message";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return 0;
    }
}
```

起始页 list.jsp 改为跳转到查询信息的 servlet。

```jsp
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>MessageBoard</title>
    <meta http-equiv="refresh" content="0;url=<%=request.getContextPath()%>/message/list.do">
</head>
</html>
```

xml 中的配置。

```xml
    <servlet>
        <servlet-name>MessageListServlet</servlet-name>
        <servlet-class>me.seriouszyx.jdbc.servlet.MessageListServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>MessageListServlet</servlet-name>
        <url-pattern>/message/list.do</url-pattern>
    </servlet-mapping>
```

启动服务器，即可进入主页面，显示留言板消息。

![TIM截图20180907204237.png](https://i.loli.net/2018/09/07/5b92724a4fe1d.png)





