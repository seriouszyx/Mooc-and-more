#   JDBC 案例——留言板

<!-- TOC -->

- [需求分析](#需求分析)
- [工程创建](#工程创建)
- [封装数据库](#封装数据库)
- [数据库操作工具类](#数据库操作工具类)
- [查询及分页的处理](#查询及分页的处理)
- [用户登录](#用户登录)
- [查看与修改个人信息](#查看与修改个人信息)
- [新建留言](#新建留言)

<!-- /TOC -->

##  需求分析

![TIM截图20180906203135.png](https://i.loli.net/2018/09/07/5b925ac729499.png)

##  工程创建

先创建文件目录。

![TIM截图20180907190752.png](https://i.loli.net/2018/09/07/5b925c18557c3.png)

为了方便，前端静态页面在`资料`中给出，复制到相应的文件夹即可，这样可以将注意力集中到后端实现。

**注意将 `lib` 中的 jar 包导入工程。**

##  封装数据库

新建名为 `message_board` 的数据库，并打开`资料`中的.sql文件，自动生成表并插入数据。

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



##  用户登录

点击主页面的登录按钮，通过 `LoginPromptServlet` 跳转到登录页面，配置 xml 文件，路径为 `/login.do`。

```java
/**
 *  登录前置操作Servlet
 * */
@WebServlet(name = "LoginPromptServlet")
public class LoginPromptServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/biz/login.jsp").forward(request, response);
    }

}
```

验证码所需 Servlet 在资料中，复制到相应目录下，并在 xml 文件中配置，访问路径为 `/verificationCode.do`。

创建 User 类进行数据库中 user 表的封装。

```java
public class User {

    private long id;

    private String name;

    private String password;

    private String realName;

    private Date birthday;

    private String phone;

    private String address;
    
    ...
}
```

下面完成登录所需 Servlet，从表格中获取用户名和密码，在数据库中查询，并根据查询结果跳转到相应的页面。

```java
/**
 *  登录Servlet
 * */
@WebServlet(name = "LoginServlet")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userService.login(username, password);
        if (user != null) {
            request.getSession().setAttribute("user", user);
            request.getRequestDispatcher("/message/list.do").forward(request, response);
        } else {
            request.getRequestDispatcher("/login.do").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        userService = null;
    }
}
```

用户登录的 Service 层。

```java
public class UserService {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

}
```

用户登录的 DAO 层。

```java
public class UserDAO {

    /**
     * @Author Yixiang Zhao
     * @Description 用户登录
     * @Date 21:28 2018/9/7
     * @Param [username, password]
     * @return me.seriouszyx.jdbc.bean.User 成功返回Bean，失败返回空
     **/
    public User login(String username, String password) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from user where username  = ? and password = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRealName(rs.getString("real_name"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            System.out.println("登录失败....");
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return user;
    }

}
```

完成后打开服务器，点击登录按钮，进入登录页面，输入数据库中的用户名和密码，进入主页面。

![TIM截图20180908131831.png](https://i.loli.net/2018/09/08/5b935be722c99.png)

##  查看与修改个人信息

我们需要创建一个新的 servlet 用于进行用户信息的相关操作。

```java
@WebServlet(name = "UserServlet")
public class UserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathName = request.getServletPath();
        if (Objects.equals("/userInfo.do", pathName)) {
            request.getRequestDispatcher("/WEB-INF/views/biz/user.jsp").forward(request, response);
        } else if (Objects.equals("/editUserPrompt.do", pathName)) {
            Long id = Long.valueOf(request.getParameter("id"));
            User user = userService.getUserById(id);
            if (null != user) {
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/biz/edit_user.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/views/biz/user.jsp").forward(request, response);
            }
        } else if (Objects.equals("/editUser.do", pathName)) {
            Long id = Long.valueOf(request.getParameter("id"));
            String name = request.getParameter("name");
            String password = request.getParameter("password");
            String realName = request.getParameter("realName");
            String birthday = request.getParameter("birthday");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPassword(password);
            user.setRealName(realName);
            try {
                user.setBirthday(new SimpleDateFormat("yyyy-mm-dd").parse(birthday));
            } catch (ParseException e) {
                System.out.println("格式化字段失败");
                e.printStackTrace();
            }
            user.setPhone(phone);
            user.setAddress(address);
            boolean res = userService.updateUser(user);
            if (res == true) {
                request.getSession().setAttribute("user", user);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/biz/user.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
        }

    }

    @Override
    public void destroy() {
        super.destroy();
        userService = null;
    }
}

```

根据访问路径的不同，该 servlet 的 service 方法中实现了不同功能的代码：

*   userInfo.do         用于跳转到用户信息页面
*   editUserPrompt.do   用于跳转到修改用户信息的页面
*   editUser.do         用于修改用户的信息

DAO 层新增了两个方法，可以自行抽取公共代码。

```java

    /**
     * @Author Yixiang Zhao
     * @Description 根据用户id查询用户信息
     * @Date 14:15 2018/9/8
     * @Param [id]
     * @return me.seriouszyx.jdbc.bean.User
     **/
    public User getUserById(Long id) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from user where id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRealName(rs.getString("real_name"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
            }
        } catch (Exception e) {
            System.out.println("查询用户信息失败....");
            e.printStackTrace();
        } finally {
            ConnectionUtil.release(rs, pstmt, conn);
        }

        return user;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 修改用户信息
     * @Date 14:33 2018/9/8
     * @Param [user]
     * @return boolean
     **/
    public boolean updateUser(User user) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "update user set username = ?, password = ?, real_name = ?," +
                "     birthday = ?, phone = ?, address = ? where id = ?";
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRealName());
            pstmt.setDate(4, new Date(user.getBirthday().getTime()));
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setLong(7, user.getId());
            pstmt.execute();
        } catch (Exception e) {
            System.out.println("查询用户信息失败....");
            e.printStackTrace();
            return false;
        } finally {
            ConnectionUtil.release(null, pstmt, conn);
        }

        return true;
    }
```

同时新增了一个用于编码处理的 Filter。

```java
@WebFilter(filterName = "CharsetEncodingFilter")
public class CharsetEncodingFilter implements Filter {

    private String encoding;

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        servletRequest.setCharacterEncoding(encoding);
        chain.doFilter(servletRequest, servletResponse);
    }

    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("encoding");
    }

}
```

Filter 在 web.xml 中的配置。

```xml
    <filter>
        <filter-name>CharsetEncodingFilter</filter-name>
        <filter-class>me.seriouszyx.jdbc.filter.CharsetEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CharsetEncodingFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>
```

点击主页面“我的信息”即可进入用户信息面板。

![TIM截图20180908150758.png](https://i.loli.net/2018/09/08/5b9375892f3d3.png)

点击“修改”可进入修改页面，可以保存用户信息。

![TIM截图20180908150809.png](https://i.loli.net/2018/09/08/5b9375892fcd2.png)

##  新建留言

新建用户留言信息处理的 Servlet，并在 web.xml 中配置访问路径。

```java
/**
 *  用户留言信息处理Servlet
 * */
@WebServlet(name = "MessageServlet")
public class MessageServlet extends HttpServlet {

    private MessageService messageService;

    @Override
    public void init() throws ServletException {
        super.init();
        messageService = new MessageService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathName = request.getServletPath();
        if (Objects.equals("/addMessagePrompt.do", pathName)) {
            request.getRequestDispatcher("/WEB-INF/views/biz/add_message.jsp").forward(request, response);
        } else if (Objects.equals("/addMessage.do", pathName)) {
            User user = (User) request.getSession().getAttribute("user");
            if (null == user) {
                request.getRequestDispatcher("/message/list.do").forward(request, response);
            } else {
                String title = request.getParameter("title");
                String content = request.getParameter("content");
                boolean res = messageService.addMessage(new Message(user.getId(), user.getName(), title, content));
                if (res) {
                    request.getRequestDispatcher("/message/list.do").forward(request, response);
                } else {
                    request.getRequestDispatcher("/WEB-INF/views/biz/add_message.jsp").forward(request, response);
                }
            }
        } else{
            request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        messageService = null;
    }
}
```

其中，

*   addMessagePrompt.do 为跳转到新建留言页面。
*   addMessage.do 为进行新建留言的相关操作。

在 MessageDAO 中增加新的方法，来保存用户新建立的留言信息。

```java
    /**
     * @Author Yixiang Zhao
     * @Description 保存留言信息
     * @Date 15:41 2018/9/8
     * @Param [message]
     * @return boolean
     **/
    public boolean save(Message message) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "insert into message (user_id, username, title, content, create_time) values(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, message.getUserId());
            pstmt.setString(2, message.getUsername());
            pstmt.setString(3, message.getTitle());
            pstmt.setString(4, message.getContent());
            pstmt.setTimestamp(5, new Timestamp(message.getCreateTime().getTime()));
            pstmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("保存留言信息失败....");
            return false;
        } finally {
            ConnectionUtil.release(null, pstmt, conn);
        }

        return true;
    }
```

启动服务器，点击主页面的“点我留言”即可进入新建留言页面。

![TIM截图20180908154315.png](https://i.loli.net/2018/09/08/5b937dafd859f.png)

选择“发布留言”即可返回主页面，并显示新增加的留言信息。

![TIM截图20180908154326.png](https://i.loli.net/2018/09/08/5b937daff1e2d.png)