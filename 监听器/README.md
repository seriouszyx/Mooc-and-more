#   Java 监听器

>	与慕课网[监听器](https://class.imooc.com/sc/31/series)搭配使用效果更佳。

<!-- TOC -->

- [监听器简介](#监听器简介)
    - [1.  监听器定义](#1--监听器定义)
    - [2.  常见应用场景](#2--常见应用场景)
    - [3.  实现步骤](#3--实现步骤)
- [防止重复登陆案例](#防止重复登陆案例)
    - [1.  案例说明](#1--案例说明)
    - [2.  页面导入](#2--页面导入)
    - [3.  登录权限过滤验证](#3--登录权限过滤验证)
    - [4.  用户信息存储实现](#4--用户信息存储实现)
    - [5.  监听功能实现](#5--监听功能实现)

<!-- /TOC -->


##  监听器简介

### 1.  监听器定义

![TIM截图20180810145823.png](https://i.loli.net/2018/08/10/5b6d38cfedd83.png)

### 2.  常见应用场景

*   统计在线人数
*   页面访问量的统计
*   应用启动时完成信息初始化工作
*   与 Spring 结合

### 3.  实现步骤

1.  编写 Java 类实现监听器的接口，并实现其接口方法
2.  在 web.xml 文件中对实现监听器类进行注册


##  防止重复登陆案例

假设用户在一个电脑上登陆，又在另外一个电脑上登陆，应用就会将先前的用户注销登陆，并输出提示信息。

### 1.  案例说明

*   通过过滤器实现登陆控制，未登录用户不能访问系统首页
*   用户登录，将登录名储存在 session 里
*   登陆监听器监听 session 属性中登陆值的变化
*   若登录用户用户名已登陆系统，清除前次登陆信息

### 2.  页面导入

将 JavaPage 下面的文件复制到 web 目录下，启动服务器，进入登陆页面，随机输入用户名和密码（没有表单校验），进入主页面。

![TIM截图20180810155028.png](https://i.loli.net/2018/08/10/5b6d43efd5291.png)

### 3.  登录权限过滤验证

新建一个过滤器，在进入主页面时检查用户是否登陆，如果未登录，返回登陆页面并提示错误信息，否则进入主页面。

```java
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
```

web.xml 文件配置信息

```xml
 <filter>
    <filter-name>SessionFilter</filter-name>
    <filter-class>me.seriouszyx.filter.SessionFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>SessionFilter</filter-name>
    <url-pattern>/main.jsp</url-pattern>
</filter-mapping>
```

### 4.  用户信息存储实现

为了用不同的浏览器检查用户是否登陆，我们要将用户信息存入服务端的缓存中，创建 LoginCache 类，使用单例设计模式实现缓存。

```java
public class LoginCache {

    private static LoginCache instance = new LoginCache();

    private Map<String, String> loginUserSession = new HashMap<>();
    private Map<String, HttpSession> loginSession = new HashMap<>();

    private LoginCache() {

    }

    public static LoginCache getInstance() {
        return instance;
    }

    /**
     * 通过登录名获取对应登录用户的sessionId
     * @param username
     * @return
     */
    public String getSessionIdByUsername(String username){
        return loginUserSession.get(username);
    }

    /**
     * 通过sessionId获取对应的session对象
     * @param sessionId
     * @return
     */
    public HttpSession getSessionBySessionId(String sessionId){
        return loginSession.get(sessionId);
    }

    /**
     * 存储登录名与对应的登录sessionID至缓存对象
     * @param username
     * @param sessionId
     */
    public void setSessionIdByUserName(String username,String sessionId){
        loginUserSession.put(username, sessionId);
    }

    /**
     * 存储sessionId与对应的session对象至缓存对象
     * @param sessionId
     * @param session
     */
    public void setSessionBySessionId(String sessionId,HttpSession session){
        loginSession.put(sessionId, session);
    }

}
```

通过用户名获取到 sessionId，再通过 sessionId 获取 session。

![TIM截图20180810155859.png](https://i.loli.net/2018/08/10/5b6d768f44da7.png)

### 5.  监听功能实现



![TIM截图20180810160352.png](https://i.loli.net/2018/08/10/5b6d768f47c9c.png)

我们新建一个 LoginSessionListener 实现 HttpSessionAttributeListener 接口的监听器，每次登陆时，都检查服务端缓存中是否有该用户信息，如果有，则意味着用户重复登陆。

其中有一个私有成员变量用以记录用户名所在 session 的名称。

```java
private static final String LOGIN_USER = "loginUser";
```

然后重写 attributeAdded 方法

```java
@Override
    public void attributeAdded(HttpSessionBindingEvent hsbe) {
        String attrName = hsbe.getName();//监听到session属性值发生添加操作，获取对应操作的属性名

        if(LOGIN_USER.equals(attrName)){//若属性名为登录属性名，判定为用户登录操作
            String attrVal = (String)hsbe.getValue();//获取添加的属性值，即用户登录名
            HttpSession session = hsbe.getSession();//该次操作的session对象
            String sessionId = session.getId();//该次操作的session对象ID
            String sessionId2 = LoginCache.getInstance().getSessionIdByUsername(attrVal);//从缓存对象里面，获得该用户登录名对应的sessionID值
            if(null == sessionId2){//未获得结果，不需要清理前次登录用户会话信息
            }else{
                HttpSession session2 = LoginCache.getInstance().getSessionBySessionId(sessionId2);//获取前次该用户登录对应的session对象
                session2.invalidate();//清理前次登录用户会话存储信息，使得前次登录失效
            }

            //完成该次登录用户登录名、sessionID，session对象的缓存对象存储
            LoginCache.getInstance().setSessionIdByUserName(attrVal, sessionId);
            LoginCache.getInstance().setSessionBySessionId(sessionId, session);
        }
    }
```

在 web.xml 文件中配置该 listener

```xml
<listener>
    <listener-class>me.seriouszyx.listener.LoginSessionListener</listener-class>
</listener>
```

这样用户在一种浏览器登陆后，再从另外一种浏览器登陆，监听器就会将先前登陆的 session 删除，提示用户已从另一个地点登陆。