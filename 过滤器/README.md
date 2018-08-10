#   Java 过滤器

>	与慕课网[过滤器](https://class.imooc.com/sc/31/series)搭配使用效果更佳。


<!-- TOC -->

- [过滤器概述](#过滤器概述)
    - [1.  工作流程](#1--工作流程)
    - [2.  生命周期](#2--生命周期)
    - [3.  实现步骤](#3--实现步骤)
    - [4.  过滤器链](#4--过滤器链)
- [留言板实例](#留言板实例)
    - [1.  留言板功能实现](#1--留言板功能实现)
    - [2.  过滤器解决中文编码问题](#2--过滤器解决中文编码问题)
    - [3.  过滤器实现用户登陆安全控制](#3--过滤器实现用户登陆安全控制)
- [dispatcher元素](#dispatcher元素)

<!-- /TOC -->


##  过滤器概述

### 1.  工作流程

![TIM截图20180809213134.png](https://i.loli.net/2018/08/09/5b6c424d72634.png)

### 2.  生命周期

*   web 应用程序启动时，web 服务器创建 Filter 的实例对象，以及对象的初始化
*   当请求访问与过滤器关联的 web 资源的时候，过滤器拦截请求，完成指定功能
*   Filter 对象创建后会驻留在内存，在 web 应用移除或服务器停止时才销毁

### 3.  实现步骤

1.  编写 java 类实现 Filter 接口，并实现其 doFilter 方法
2.  在 web.xml 文件中完成对 filter 类的注册，并设置所拦截的资源

### 4.  过滤器链

*   在一个 web 应用中，多个过滤器组合起来称之为一个过滤器链
*   过滤器的调用顺序取决于过滤器在 web.xml 文件中的注册顺序

##  留言板实例

### 1.  留言板功能实现

打开 my-filterDemo 工程，其中 index.jsp 为登陆页面，这个项目并没有登陆验证，重点是实现留言版的功能，所以你可以输入任何用户名或者密码进行登陆。

登陆请求发送到 login.jsp 将用户名存入 session

```jsp
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    
    session.setAttribute("loginUser", username);		//模拟登录操作，在session中存入登录用户的用户名
    response.sendRedirect(request.getContextPath()+"/message.jsp");//重定向至留言板页面
%>
```

为了完成留言板的功能，我们需要对留言的数据进行封装，建立一个 Message 类，包含两个私有属性，留言的标题和留言的内容。


```java
public class Message {
    private String title;
    private String content;
    ...
}
```

当 login.jsp 重定向到留言板页面 message.jsp 时，会从 session 中获取用户名并在 输入栏显示。

```jsp
	<label>
	<span>留言人 :</span>
	<input id="user" type="text" name="user" value="<%=user %>" readonly/>
	</label>
```

输入留言信息并提交表单，将会把请求发往 messageSub.jsp，在这里将用户填写的信息封装成 Message对象，再存入 Message 的集合中，其中 Message 集合存入 session。

```jsp
<% 
	String title = request.getParameter("title");
	String content = request.getParameter("content");
	
	Message message = new Message();
	message.setTitle(title);
	message.setContent(content);
	
	List<Message> messages = (List<Message>)session.getAttribute("messages");
	if(messages == null){
		messages = new ArrayList<Message>();
		session.setAttribute("messages", messages);
	}
	
	messages.add(message);//留言列表信息存储至session中
	
	response.sendRedirect(request.getContextPath()+"/message.jsp?subFlag=1");//重定向至留言板页面
%>
```

重定向回 message.jsp，从 session 中取出 Message 集合，并遍历集合显示信息。

```jsp
<%
    if(messages!=null){
        for(Message message : messages){
        %>
            <tr>
                <td><%=user %></td>
                <td><%=message.getTitle() %></td>
                <td><%=message.getContent() %></td>
            </tr>
        <%
        }
    }
%>
```

此时的留言板功能已基本上完成，但是会出现中文乱码问题，我们利用**过滤器**来解决这个问题。

![TIM截图20180810093417.png](https://i.loli.net/2018/08/10/5b6ceba287862.png)

### 2.  过滤器解决中文编码问题

首先我们呢要创建一个 CharacterEncodingFilter 类实现 Filter 接口，并实现接口中的类。其中 init 方法可以接受一个初始化参数，这个参数在 web.xml 中配置，所以定义一个私有成员变量来保存这个初始化参数。

```java
public class CharacterEncodingFilter implements Filter {

    private FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
```

然后配置 web.xml 文件，<init-param> 中写初始化参数的名称和值，<url-pattern> 表示将所有请求都进行过滤。

```xml
    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>me.seriouszyx.filter.CharacterEncodingFilter</filter-class>

        <init-param>
            <param-name>charset</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

然后在 CharacterEncodingFilter 类中实现 doFilter 方法，设置 request 额字符集，并通过过滤器链向后传递请求。

```java
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding(config.getInitParameter("charset"));
        filterChain.doFilter(servletRequest, servletResponse);
    }
```

中文乱码问题得到解决。

![TIM截图20180810100322.png](https://i.loli.net/2018/08/10/5b6cf2725ecc1.png)

### 3.  过滤器实现用户登陆安全控制

为了防止用户没有登陆就进入 message.jsp 页面，需要对其添加一个过滤器进行安全控制。

```java
public class SessionFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String loginUser = (String) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            // 用户未登录
            response.sendRedirect(request.getContextPath() + "/index.jsp?flag=1");
            return;
        } else {
            chain.doFilter(request, response);
            return;
        }

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
```

需要注意的是要把 req 和 resp 转换成 HttpServlet... 的形式。

在 web.xml 中配置时只拦截发往 message.jsp 的请求。

```xml
    <filter>
        <filter-name>SessionFilter</filter-name>
        <filter-class>me.seriouszyx.filter.SessionFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>SessionFilter</filter-name>
        <url-pattern>/message.jsp</url-pattern>
    </filter-mapping>
```

这样用户在地址栏直接输入 message.jsp 时将会重定向到登陆页面，并弹出提示信息。

##  dispatcher元素

2.4版本的servlet规范在部属描述符中新增加了一个`<dispatcher>`元素，这个元素有四个可能的值：即REQUEST,FORWARD,INCLUDE和ERROR，可以在一个`<filter-mapping>`元素中加入任意数目的`<dispatcher>`，使得filter将会作用于直接从客户端过来的request（REQUEST），通过forward过来的request（FORWARD），通过include过来的request（INCLUDE）和通过`<error-page>`过来的request（ERROR）。如果没有指定任何`< dispatcher >`元素，默认值是REQUEST。 

注意：`<dispatcher></dispatcher>`必须写在filter-mapping的最后。dispatcher的前提条件当然是要先满足url-pattern，然后dispatcher有四种可能的属性： 

1、REQUEST 

只要发起的操作是一次HTTP请求，比如请求某个URL、发起了一个GET请求、表单提交方式为POST的POST请求、表单提交方式为GET的GET请求。一次重定向则前后相当于发起了两次请求，这些情况下有几次请求就会走几次指定过滤器。 


2、FOWARD 

只有当当前页面是通过请求转发转发过来的情形时，才会走指定的过滤器 

3、INCLUDE 

只要是通过<jsp:include page="xxx.jsp" />，嵌入进来的页面，每嵌入的一个页面，都会走一次指定的过滤器。 

4、ERROR 

假如web.xml里面配置了<error-page></error-page>：


```xml
<error-page>
    <error-code>400</error-code>
    <location>/filter/error.jsp</location>
</error-page>

<error-page>
    <error-code>404</error-code>
    <location>/filter/error.jsp</location>
</error-page>

<error-page>
    <error-code>500</error-code>
    <location>/filter/error.jsp</location>
</error-page>
```


意思是HTTP请求响应的状态码只要是400、404、500三种状态码之一，容器就会将请求转发到error.jsp下，这就触发了一次error，走进了配置的DispatchFilter。需要注意的是注意一点的是，虽然把请求转发到error.jsp是一次forward的过程，但是配置成<dispatcher>FORWARD</dispatcher>并不会走DispatchFilter这个过滤器。 

这四种dispatcher方式可以单独使用，也可以组合使用，配置多个<dispatcher></dispatcher> 即可。

>   dispatcher元素部分转自[CSDN](https://blog.csdn.net/xiaokang123456kao/article/details/72885171)