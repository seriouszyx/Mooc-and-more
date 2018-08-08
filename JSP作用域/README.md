#   JSP 内置对象

##  一、简介

**jsp 内置对象**（又叫隐含对象，有9个内置对象）：

不需要预先声明就可以在脚本代码和表达式中随意使用。


**作用域**：

*   pageContext
*   request
*   session
*   application


##  二、 request,out,response

### 1.概念

*   request
>   封装了由 WEB 浏览器或其他客户端生成 HTTP 请求的细节（参数，属性，头标和数据）

*   out
>   代表输出流对象

*   response
>   封装了返回到 HTTP 客户端的输出，向页面作者提供设置响应头标和状态码的方式

*   request 作用域
>   用户的请求周期。在相邻两个 web 资源之间共享同一个 request 请求对象时使用


### 2.  人事管理系统

我们将一步步地构建一个小型的管理系统。

首先创建雇员的类，包含四个属性，并为它添加构造函数以及 get/set 方法

```java
    public class Emp {
        private String account;
        private String name;
        private String password;
        private String email;
    }
```

然后再创建虚拟的数据库，并为它提供一个判断能否成功登陆的方法

```java
public class DBUtil {
    /** 创建虚拟数据库 */
    public static Map<String, Emp> map = new HashMap<String, Emp>();
    static {
        map.put("101", new Emp("101", "AA" , "123456", "AA@gmail.com"));
        map.put("102", new Emp("102", "BB" , "123456", "BB@gmail.com"));
        map.put("103", new Emp("103", "CC" , "123456", "CC@gmail.com"));
        map.put("104", new Emp("104", "DD" , "123456", "DD@gmail.com"));
    }

    /** 判断用户名和密码是否正确 */
    public static boolean selectEmpByAccountAndPassword (Emp emp) {
        boolean flag = false;
        for (String key : map.keySet()) {
            Emp e = map.get(key);
            if (emp.getAccount().equals(e.getAccount()) && emp.getPassword().equals(e.getPassword())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
```

然后是登陆页面的 jsp 文件，这里给出主要的表单部分代码

```html
<form action="control.jsp" method="post">
        <table align="center">
            <tr>
                <td>账号：</td>
                <td><input type="text" name="account" /></td>
            </tr>
            <tr>
                <td>密码：</td>
                <td><input type="password" name="password" /></td>
            </tr>
            <tr>
                <td><input type="submit" value="登陆" /></td>
            </tr>
        </table>
    </form>
```

然后写出跳转的页面，它会通过 request 对象获取账户和密码，并判断是否正确做出响应。


```jsp
<!-- 获取账号以及密码，并且调用DBUtil当中的方法来判断是否存在指定的信息 -->
    <%
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        /*
            out.println("account：" + account);
            out.println("paaword:" + password);
        */
        Emp emp = new Emp(account, null, password, null);
        Boolean flag = DBUtil.selectEmpByAccountAndPassword(emp);
        Map<String, Emp> map = DBUtil.map;
        if (flag == true) {
    %>
        <h3 align="center">欢迎来到人事管理系统的首页</h3>
        <hr />
        <table align="center" border="1" width="500px">
            <tr>
                <td>
                    账号：
                </td>
                <td>
                    姓名：
                </td>
                <td>
                    邮箱：
                </td>
            </tr>
            <%
                for (String key : map.keySet()) {
                    Emp e = map.get(key);
                    %>
                        <tr>
                            <td>
                                <%=e.getAccount() %>
                            </td>
                            <td>
                                <%=e.getName() %>
                            </td>
                            <td>
                                <%=e.getEmail() %>
                            </td>
                        </tr>
                    <%
                }
            %>
        </table>
    <%
        } else {
            out.println("用户名和密码错误");
        }
    %>
```


再次说明一下 request 的作用域，request 作用域的隐含对象时 request。**因为请求对象对于每一个客户请求都是不同的，所以对于每一个新的请求，都要重新创建和删除这个范围内的对象**。

具体来说，如果在 `logon.jsp` 中为 request 设置一个属性，再在 `control.jsp` 中获取这个对象的值，获取的结果是 null。

因为打开 login.jsp 时就已经向服务端发起了一个请求，创建了一个 request 对象，设置完属性后，执行登陆操作，跳转到 control.jsp 页面，相当于向服务端发起了一个新的请求，这时创建了一个新的 request 对象，而新的 request 对象并没有以前存的属性，所以获取结果为 null。

不过，我们可以这样传递 request 

```jsp
    <%
        request.setAttribute("name", "zyx");
        request.getRequestDispatcher("result.jsp").forward(request, response);
    %>
```
    
进入 logon.jsp 页面后，为 request 对象设置属性，然后直接将请求转发到 result.jsp 页面，然后在 result.jsp 中获取数据

```jsp
<body>
    从 request 作用域获取的数据<%=request.getAttribute("name") %>
</body>
```

访问 logon.jsp 页面，将请求转发到 result.jsp 页面，可以获取 request 的数据，**但请求仍是 logon.jsp 页面**。

![TIM截图20180808102912.png](https://i.loli.net/2018/08/08/5b6a558a7d661.png)


##  三、    page,pageContext

### 1.  pageContext

       页面的上下文对象。提供了转发请求到其他资源的和包含其他资源的方法，提供获取其他内置对象的方法。



*   下面举例说明 pageContext 的**请求转发**功能。

```jsp
  <body>
      <%
          pageContext.forward("a.jsp?name=zyx");
      %>
  </body>
```

发起请求访问该页面的时候，将请求转发给 a.jsp 页面，并带上参数 name。

```jsp
<body>
    <%=request.getParameter("name") %>
</body>
```

然后在 a.jsp 中就可以获取到传递的参数 name。

*   下面说明 pageContext 的包含功能

我们可以新建一个 header.jsp 

```jsp
<body>
    这是一个 Header 头部份
</body>
```

然后在 index.jsp 中包含它

```jsp
  <body>
      <%
          pageContext.include("header.jsp");
          // pageContext.forward("a.jsp?name=zyx");
      %>
  </body>
```

即可显示以下内容：

![TIM截图20180808112511.png](https://i.loli.net/2018/08/08/5b6a62a18d6aa.png)


*   下面说明 pageContext 获取其他内置对象的方法

上述 a.jsp 也可以这样写

```jsp
<body>
    <%=pageContext.getRequest().getParameter("name") %>
</body>
```

pagrContext 可以获取当前请求的 request 对象，并获取它的属性。

### 2. page 对象

>   代表了正在运行的由 jsp 文件产生的类对象。

```jsp
    page 的基本信息：<%=this.getServletInfo() %>
    <br />
```

在 logon.jsp 中加入上述内容，则在页面上会打印 logon.jsp 的基本信息

![TIM截图20180808113307.png](https://i.loli.net/2018/08/08/5b6a64cf879a6.png)

### 3.  pageContext 作用域

>   当前执行页面

```jsp
    <%
        pageContext.setAttribute("name", "zyx");
    %>

    pageContext 获取到的数据：<%=pageContext.getAttribute("name") %>
    <br />
```

可以使用 pageContext 设置属性，而且只能在当前页面获取到数据（**请求转发后不能获取到之前设置的 pageContext 的数据**）。

pageContext 作用域用得并不是很多，所以简单了解即可。

##  四、    session,config,exception

### 1.  session

>   主要用于跟踪会话（频繁使用）

**会话**
>   是代表用户第一次进入当前系统直到退出系统或关闭浏览器，在此期间与服务器的一系列交互。


**session 作用域**
>   会话期间

回到刚才的人事管理系统案例，如果用户成功登陆的话，我们将用户信息储存到 session 里，在 control.jsp 页面就能获取到 session 里面的数据。


```jsp
        ...
        if (flag == true) {
            session.setAttribute("account", account);
    %>
        <h3 align="right">登陆账户：<%=session.getAttribute("account") %></h3>
        <h3 align="center">欢迎来到人事管理系统的首页</h3>
        <hr />
        ...
```

然后为表单再添加一列修改数据按钮，可以跳转到新建的 `update.jsp` 页面。

在 update.jsp 页面中，同样可以通过 session 获取到员工信息。

```jsp
    <h3 align="center">员工更新页面</h3>
    <hr />
    <h3 align="center">登陆账号：<%=session.getAttribute("account") %></h3>
    <form>
        <table align="center" border="1" width="500px">
            <tr>
                <td>账号</td>
                <td><input type="text" name="acount" /></td>
            </tr>
            <tr>
                <td>名字</td>
                <td><input type="text" name="name" /></td>
            </tr>
            <tr>
                <td>邮箱</td>
                <td><input type="text" name="email" /></td>
            </tr>
        </table>
    </form>
```

### 2.  config

>   获取配置信息

我们在 web.xml 进行如下配置

```xml
    <servlet>
        <servlet-name>logon</servlet-name>
        <jsp-file>/logon.jsp</jsp-file>
        <init-param>
            <param-name>age</param-name>
            <param-value>100</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>logon</servlet-name>
        <url-pattern>/logon.do</url-pattern>
    </servlet-mapping>
```

然后在 logon.jsp 中加入下面内容，即可获取配置信息

```jsp
    config对象：<%=config.getServletName() %>
    <br />
    config初始化参数：<%=config.getInitParameter("age") %>
```

### 2.  exception

>   异常对象

现在考虑用户名或密码错误的情况，我们选择抛出一个异常

```jsp
throw new Exception("账号和密码错误");
```

这个时候登陆错误的话页面会显示 500 异常，我们要处理这个异常，在 page 中声明 errorPage="error.jsp"，这样出现异常就会由 error.jsp 处理

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java"  errorPage="error.jsp" %>
```

创建 error.jsp 并使用 exception 对象处理错误信息。

```jsp
<body>
    <%= exception.getMessage()%>
</body>
```

另外 error.jsp 中要声明 `isErrorPage="true"`

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
```

exception 对象有以下两点**注意事项**：

*   exception 对象只能在错误页面中使用，page 加入一个属性 isErrorPage="true"
*   有一个页面出现了异常，在页面指定一个错误处理的页面，page 指令中的 errorPage 来指定。


##  五、    application

### 1.  概念
*   application 对象
>   提供了关于服务器版本，应用级初始化参数和应用内资源绝对路径方式

*   application 作用域
>   web 容器的生命周期

### 2.  应用——获取当前系统访问量

当用户登陆成功时，从 application 中获取 count 的值，如果为空，则是第一次登陆；否则，将 count + 1 之后再存入 application 中

```jsp
...
if (flag == true) {
            Object o = application.getAttribute("count");
            if (o == null) {
                // 第一次登陆
                application.setAttribute("count", 1);
            } else {
                int count = Integer.parseInt(o.toString());
                application.setAttribute("count", count+1);
            }

            session.setAttribute("account", account);
    %>
        <h3 align="center">访问量：<%=application.getAttribute("count") %></h3>
...
```

##  六、    完善人事管理系统

点击修改按钮时，传递该条目的信息

```jsp
<a href="update.jsp?account=<%=e.getAccount() %>&name=<%=e.getName() %>&email=<%=e.getEmail() %>">修改 </a>
```

在 update.jsp 中进行数据回显

```jsp
    <form action="update_control.jsp">
        <table align="center" border="1" width="500px">
            <tr>
                <td>账号</td>
                <td><input type="text" name="account" value="<%=request.getParameter("account") %>" /></td>
            </tr>
            <tr>
                <td>名字</td>
                <td><input type="text" name="name" value="<%=request.getParameter("name") %>" /></td>
            </tr>
            <tr>
                <td>邮箱</td>
                <td><input type="text" name="email" value="<%=request.getParameter("email") %>" /></td>
            </tr>
            <tr>
                <td><input type="submit" value="修改"/></td>
            </tr>
        </table>
    </form>
```

对 DBUtil 中的 map 中的值进行修改，并提示修改成功信息。

```jsp
<body>
    <%
        Map<String, Emp> map = DBUtil.map;
        Emp emp = map.get(request.getParameter("account"));
        emp.setName(request.getParameter("name"));
        emp.setEmail(request.getParameter("email"));
    %>
    <h3 align="center">修改员工信息成功</h3>
</body>
```