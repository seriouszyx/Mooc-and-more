#   web 注册登录案例

>	与慕课网[web实现登录注册功能](https://class.imooc.com/sc/31/series)搭配使用效果更佳。

<!-- TOC -->

- [MVC 概述](#mvc-概述)
- [案例准备](#案例准备)
    - [1.  导入页面](#1--导入页面)
    - [2.  用户实体的创建](#2--用户实体的创建)
    - [3.  初始化集合](#3--初始化集合)
- [注册功能实现](#注册功能实现)
    - [1.  文件上传规范](#1--文件上传规范)
    - [2.  普通表单项的接收](#2--普通表单项的接收)
    - [3.  文件上传项的接收](#3--文件上传项的接收)
    - [4.  用户名校验](#4--用户名校验)
- [登陆功能实现](#登陆功能实现)
    - [1.  登录功能的代码实现](#1--登录功能的代码实现)
     - [2.  记住用户名](#2--记住用户名)

<!-- /TOC -->


##  MVC 概述

Servlet + JSP + JavaBean (MVC) ---->    复杂web程序

*   Servlet：处理用户请求
*   JSP：数据显示
*   JavaBean：数据封装

MVC 模式

*   M：Model 模型层 —— JavaBean
*   V：View 视图层 —— JSP
*   C：Controller 控制层 —— Servlet

用户的请求提交到 Servlet，由 Servlet 统一调度 JavaBean 封装和处理数据，由 JSP 进行数据显示。

![TIM截图20180808183015.png](https://i.loli.net/2018/08/08/5b6ac65232110.png)

##  案例准备

### 1.  导入页面

将 javaPage 下的文件复制到 web 目录下，启动服务器，在浏览器地址栏输入`http://localhost:8080/regist.jsp`（端口号因人而异），即可访问到注册页面。

### 2.  用户实体的创建

我们新建一个 User 类，并为它添加以下属性，再添加 get/set 和 toString 等方法。

```java
public class User {
    private String username;
    private String password;
    private String nickname;
    private String sex;
    private String hobby;
    private String path;
}
```

然后创建一个 InitServlet，重新 init 方法。因为这个项目要创建虚拟数据库，所以创建一个包含 User 对象的 List 集合。


```java
    @Override
    public void init() throws ServletException {
        // 创建一个 List 集合，用于保存用户注册信息
        List<User> list = new ArrayList<>();
        this.getServletContext().setAttribute("list", list);
    }
```

### 3.  初始化集合

为了在服务器刚启动时就调用重写的 init 方法，需要在 .xml 文件中声明

```java
    <servlet>
        <display-name>InitServlet</display-name>
        <servlet-name>InitServlet</servlet-name>
        <servlet-class>me.seriouszyx.servlet.InitServlet</servlet-class>
        <load-on-startup>2</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>InitServlet</servlet-name>
        <url-pattern>/InitServlet</url-pattern>
    </servlet-mapping>
```

其中`<load-on-startup>2</load-on-startup>`即可保证初始化 List 集合。

##  注册功能实现

### 1.  文件上传规范

文件上传条件

*   表单必须是 post 提交方式（因为 get 有大小的限制）
*   表单中必须有文件上传项，文件上传项必须有 name 属性和值
*   表单的 enctype 属性必须设置为 multipart/form-data

文件上传基本流程
1. 创建一个磁盘项工厂对象
2. 创建核心解析类
3. 解析request请求，返回List集合，集合中存放的是FileItem对象
4. 遍历集合，获得每个FileItem，判断表单项还是文件上传项

下面我们创建一个 RegistServlet 类，来处理注册的请求。

```java
public class RegistServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 数据的接收
        try {
            Map<String, String> map = new HashMap<>();
            // 1. 创建一个磁盘项工厂对象
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            // 2. 创建核心解析类
            ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
            // 3. 解析request请求，返回List集合，集合中存放的是FileItem对象
            List<FileItem> list = servletFileUpload.parseRequest(request);
            // 4. 遍历集合，获得每个FileItem，判断表单项还是文件上传项
            for (FileItem fileItem : list) {
                if (fileItem.isFormField()) {
                    // 普通表单项

                } else {
                    // 文件上传项
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }


    }
}
```

### 2.  普通表单项的接收

```java
    if (fileItem.isFormField()) {
        // 普通表单项
        String name = fileItem.getFieldName();
        String value = fileItem.getString("UTF-8");
        System.out.println(name + " " + value);
        // 复选框数据
        if ("hobby".equals(name)) {
            String hobbyValue =  fileItem.getString("UTF-8");
            hobby.add(hobbyValue);
            hobbyValue = hobby.toString().substring(1,hobby.toString().length()-1 );
            map.put(name, hobbyValue);
        } else {
            map.put(name, value);
        }
    }
```

需要说明的是，在循环 FileItem 的 List 集合前，应创建

```java
Map<String, String> map = new HashMap<>();
List<String> hobby = new ArrayList<>();
```

map 用于存储 name 和对应的 value 值，hobby 用于存储复选框选中的项。

在循环遍历结束后，将获取到的数据封装进 User 的对象中，再跳转到登陆页面。

```java
    // 封装数据到User当中
    User user = new User();
    user.setUsername(map.get("username"));
    user.setPassword(map.get("password"));
    user.setSex(map.get("sex"));
    user.setNickname(map.get("nickname"));
    user.setHobby(map.get("hobby"));
    user.setPath(url);
    
    List<User> userList = (List<User>) this.getServletContext().getAttribute("list");
    userList.add(user);
    this.getServletContext().setAttribute("list", userList);
    // 注册成功，登陆跳转页面
    request.getSession().setAttribute("username", user.getUsername());
    response.sendRedirect(request.getContextPath() + "/login.jsp");
```

### 3.  文件上传项的接收

我们先思考一个问题，如果两个人上传的图片名相同，该怎么办？

所以我们先设计一个工具类，以便获得独一无二的图片名

```java
/** 文件上传的工具类 */
public class UpdateUtils {
    /** 生成唯一的文件名 */
    public static String getUUIDFileName(String fileName) {
        // 将文件名前面部分截取： xx.jpg --> .jpg
       int idx = fileName.lastIndexOf(".");
       String extention = fileName.substring(idx);
        String uuidFileName = UUID.randomUUID().toString().replace("-", "")+extention;
        return uuidFileName;
    }
}
```

之后完成文件上传项的数据获取及存储

```java
    ...
 // 文件上传项
    // 获得文件上传名称
    String fileName = fileItem.getName();

    if (fileName != null && !"".equals(fileName)) {
        // 通过工具类获得唯一文件名
        String uuidFileName = UpdateUtils.getUUIDFileName(fileName);
        // 获得文件上传数据
        InputStream is = fileItem.getInputStream();
        // 获得文上传路径
        String path = this.getServletContext().getRealPath("/upload");
        // 将输入流对接到输出流
        url = path + "\\" + uuidFileName;
        OutputStream os = new FileOutputStream(url);
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = is.read(b)) != -1) {
            os.write(b, 0, len);
        }
        is.close();
        os.close();
    }
    ...
```

注意要确保 upload 文件夹存在。

### 4.  用户名校验

为了防止用户注册的用户名相同，在向 userList 添加新的 user 对象之前，应先检查是否有相同的用户名

```java
    List<User> userList = (List<User>) this.getServletContext().getAttribute("list");
    // 校验用户名
    for (User u : userList) {
        if (u.getUsername().equals(map.get("username"))) {
            request.setAttribute("msg", "用户名已经存在");
            request.getRequestDispatcher("/regist.jsp").forward(request, response);
            return ;
        }
    }
```

##  登陆功能实现

### 1.  登录功能的代码实现

login.jsp 发送登陆请求后，需要创建一个 servlet 接收请求

```java
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
```

在成功检测后，跳转到 success.jsp 页面

```jsp
  <%
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            int idx = user.getPath().lastIndexOf("\\");
            String fileName = user.getPath().substring(idx+1);
    %>
            <div class="login">
                <div class="header">
                    <h1>登录成功</h1>
                </div>
                <div class="content">
                    <table align="center">
                        <tr>
                            <td align="center"><img width="250px" height="250px" src="/upload/<%=fileName %>" /></td>
                        </tr>
                        <tr>
                            <td align="center">欢迎<%=user.getNickname() %>,登录成功！</td>
                        </tr>
                    </table>
                </div>
            </div>
    <%
        } else {
    %>
            <div class="login">
                <div class="header">
                    <h1>您还没有登陆，请先去<a href="/login.,jsp">登陆</a></h1>
                </div>
            </div>
    <%
        }
    %>
```

这里 eclipse 和 intellij idea 的图片获取路径略有不同，idea 可以参考[这篇文章](https://blog.csdn.net/LABLENET/article/details/51160828)。


### 2.  记住用户名

登陆成功时，判断用户是否勾选“记住用户名”选项，如果勾选，将用户名信息记录在 cookie 中。

```java
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
```

在 login.jsp 文件中，先判断是否保存了 cookie，如果保存，将 cookie 的值回显在用户名的输入框中。

```jsp
    String username = "";
    // 获得从客户端携带的所有 cookie
    Cookie[] cookies = request.getCookies();
    Cookie cookie = CookieUtils.findCookie(cookies, "username");
    if (cookie != null) {
        username = cookie.getValue();
    }

    if (session.getAttribute("username") != null) {
        username = (String) session.getAttribute("username");
    }
    ...
```


