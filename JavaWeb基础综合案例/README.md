#   Java 购物车案例

##  目录结构

创建工程后，应按照下图所示创建目录结构。

![TIM截图20180811171105.png](https://i.loli.net/2018/08/11/5b6ea833573df.png)

##  列表页及本地缓存创建

### 1.  创建列表页

我们首先在 web.xml 中设置服务器起始页面，即服务器启动时进入 index.jsp 页面。

```xml
    <welcome-file-list>
        <welcome-file>/index.jsp</welcome-file>
    </welcome-file-list>
```

我们希望 index.jsp 没有任何样式，而是跳转到一个 servlet 里面进行缓存的初始化。

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>CartDemo</title>
    <meta http-equiv="refresh" content="0;url=<%=request.getContextPath()%>/product/list.do" />
  </head>
</html>
```

创建 ProductServlet 用于初始化商品数据，讲缓存数据存进 request 对象，然后重定向到 list.jsp，在 view 下的 biz 下创建 list.jsp，先不做样式处理。

```java
/**
 *  商品（课程）控制器
 * */
@WebServlet(name = "ProductServlet")
public class ProductServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("products", null);
        req.getRequestDispatcher("/WEB-INF/biz/list.jsp").forward(req, resp);
    }
}
```

再创建一个字符编码过滤器 CharsetEncodingFilter，用以处理中文字符。

```java
/**
 *  字符编码过滤器
 * */
@WebFilter(filterName = "CharsetEncodingFilter")
public class CharsetEncodingFilter implements Filter {

    private String encoding;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        req.setCharacterEncoding(encoding);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
        this.encoding = config.getInitParameter("encoding");
    }

}
```

在 web.xml 中注册刚创建好的 servlet 和 filter

```xml
    <filter>
        <filter-name>CharsetEncodingFilter</filter-name>
        <filter-class>me.seriouszyx.cart.filter.CharsetEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CharsetEncodingFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>

    <servlet>
        <servlet-name>ProductServlet</servlet-name>
        <servlet-class>me.seriouszyx.cart.servlet.ProductServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>ProductServlet</servlet-name>
        <url-pattern>/product/list.do</url-pattern>
    </servlet-mapping>
```

### 2.  创建本地缓存

首先创建 Product 类，进行商品数据的封装。这里价格我们默认为`整数`，并为它添加 get/set等方法。

```java
/**
 *  商品（课程）
 */
public class Product {

    public Product(Long id, String tag, String name, String desc, String level, int price) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.desc = desc;
        this.level = level;
        this.price = price;
    }

    private Long id;
    /** 标签 */
    private String tag;
    /** 名称 */
    private String name;
    /** 描述 */
    private String desc;
    /** 级别 */
    private String level;
    /** 价格 */
    private int price;
}
```

创建 LocalCache 类处理本地缓存，其中数据为模拟数据，可以随机修改。

```java

/**
 *  本地缓存
 * */
public class LocalCache {

    private static Map<Long, Product> productMap = new HashMap<>();

    static {
        productMap.put(1l, new Product(1l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(2l, new Product(2l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(3l, new Product(3l, "JAVA", "JAVA基础课程-基本语法", "介绍java基本语法特性及编写规范", "初级", 219));
        productMap.put(4l, new Product(4l, "JAVA", "JAVA基础课程-JDBC", "介绍JDBC方式连接数据库", "初级", 219));
        productMap.put(5l, new Product(5l, "JAVA", "JAVA基础课程—Socket", "介绍Java网络编程Socket", "初级", 219));
        productMap.put(6l, new Product(6l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(7l, new Product(7l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(8l, new Product(8l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(9l, new Product(9l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(10l, new Product(10l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(11l, new Product(11l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(12l, new Product(12l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(13l, new Product(13l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(14l, new Product(14l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(15l, new Product(15l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(16l, new Product(16l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(17l, new Product(17l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(18l, new Product(18l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(19l, new Product(19l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(20l, new Product(20l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(21l, new Product(21l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(22l, new Product(22l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(23l, new Product(23l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(24l, new Product(24l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
        productMap.put(25l, new Product(25l, "HTML/CSS", "HTML+CSS基础课程", "HTML+CSS基础教程8小时带领大家步步深入学习标签用法和意义", "初级", 219));
    }

    public static List<Product> getProducts() {
        return new ArrayList<>(productMap.values());
    }


}
```

其中，getProducts 方法选择新创建一个 list，将数据存入其中，以免 productMap 的数据被随意修改。

这样，ProductServlet 就可以将缓存中的数据存入 request 对象。

```java
req.setAttribute("products", LocalCache.getProducts());
```

### 3.  导入静态页面

最后，将 JavaPage 文件夹下的文件复制到 web 目录下，在 web.xml 中配置 error-page。

```xml
    <error-page>
        <error-code>404</error-code>
        <location>/WEB-INF/views/error/404.jsp</location>
    </error-page>

    <error-page>
        <error-code>500</error-code>
        <location>/WEB-INF/views/error/500.jsp</location>
    </error-page>
```

将 lib 中的 jar 包导入工程，启动服务器，即可进入商品展示页面。

##  购物车

### 1.  加入购物车

下面我们要创建一个新的 servlet 以处理购物车相关操作,当选择将商品加入购物车时，向这个 servlet 发送请求。

```java
@WebServlet(name = "CartServlet")
public class CartServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.equals("/cart/cart.do", req.getServletPath())) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                LocalCache.getProduct(Long.valueOf(productId));

            }
        }
    }
}
```

代码中可以看出，在 LocalCache 中我们需要一个 getProduct 的方法，通过商品的 ID 获得商品的所有信息，所以我们创建这个方法。

```java
    public static Product getProduct(Long id) {
        return productMap.get(id);
    }
```

这个操作的目的是`将商品加入购物车`，所以最起码要有一个购物车类，以储存商品的信息。每一个购物车对象代表购物车中的一种商品，事实上 id 和 productId 相等，totalPrice 是指该商品在购物车中的总价。

构造方法的参数并没有 totalPrice，因为它可以通过 price 和 count 计算出来。同样，也要为它增加 get/set 和 toString 方法。

```java
/**
 *  購物車
 * */
public class Cart {

        /** 购物车中ID */
        private Long id;
        /** 商品ID */
        private Long productId;
        /** 商品名称 */
        private String name;
        /** 商品单价 */
        private int price;
        /** 商品数量 */
        private int count;
        /** 购物车该类商品总价 */
        private int totalPrice;
    
        public Cart(Long id, Long productId, String name, int price, int count) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.count = count;
        this.totalPrice = count * price;
    }

}
```

在 LocalCache 中自然也要有一个 Map 储存购物车的信息，key 为商品的编号，value 为 Cart 类型对象。这个 Map 指购物车中所有的商品的集合。

```java
private static Map<Long, Cart> cartMap = new HashMap<>();
```

下面添加增加购物车中商品的方法，该方法接收一个商品 ID，如果购物车中不存在此商品，那么就在 cartMap 中新增加一对商品信息，并把该商品数量设置为1。

如果已经存在此种商品，则调用 incrCart 方法，将此商品在购物车中的数量加一，并重新计算总价。

```java
    public static void addCart(Product product) {
        if (!cartMap.containsKey(product.getId())) {
            cartMap.put(product.getId(), new Cart(product.getId(), product.getId(), product.getName(), product.getPrice(), 1));
        } else {
            incrCart(product.getId());
        }
    }
```

incrCart 方法调用 Cart 类中的 incrCount 方法。

```java
    public static void incrCart(Long productId) {
        cartMap.get(productId).incrCount();
    }
```

incrCount 方法实现。

```java
    public void incrCount() {
        count++;
        this.totalPrice = price * count;
    }
```

再回到 CartServlet 中，完善 service 方法。

```java
protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.equals("/cart/cart.do", req.getServletPath())) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                Product product = LocalCache.getProduct(Long.valueOf(productId));
                LocalCache.addCart(product);
            }
            resp.sendRedirect("/cart/list.do");
        } else if (Objects.equals("/cart/list.do", req.getServletPath())) {
            req.setAttribute("carts", LocalCache.getCarts());
            req.getRequestDispatcher("/WEB-INF/views/biz/cart.jsp").forward(req, resp);
        }
    }
```

cart.do 方法增加购物车中商品后，向 list.do 发送请求。

list.do 将购物车中的商品信息集合存入 request 对象，再重定向到 cart.jsp 购物车列表页。

在 web.xml 中注册这个新的 servlet

```xml
    <servlet>
        <servlet-name>CartServlet</servlet-name>
        <servlet-class>me.seriouszyx.cart.servlet.CartServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>CartServlet</servlet-name>
        <url-pattern>/cart/list.do</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>CartServlet</servlet-name>
        <url-pattern>/cart/cart.do</url-pattern>
    </servlet-mapping>
```

启动服务器，进入主页面，将任意商品加入购物车中，就会跳转到下面的页面：


![TIM截图20180812165738.png](https://i.loli.net/2018/08/12/5b6ff68c8e6e5.png)


### 2.  购物车列表实现

购物车列表中有增加数量、减少数量和删除商品三个功能，下面面我们来一一实现。

现在 Cart.jsp 中新增三个选项，它们获得商品的编号，调用 LocalCache 中的方法执行相关操作，再重定向到 list.do。

```java
if (Objects.equals("/cart/cart.do", req.getServletPath())) {
            ...
        } else if (Objects.equals("/cart/list.do", req.getServletPath())) {
            ...
        } else if (Objects.equals("/cart/delete.do", req.getServletPath())) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                LocalCache.delCart(Long.valueOf(productId));
            }
            resp.sendRedirect("/cart/list.do");
        } else if (Objects.equals("/cart/incr.do", req.getServletPath())) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                LocalCache.incrCart(Long.valueOf(productId));
            }
            resp.sendRedirect("/cart/list.do");
        } else if (Objects.equals("/cart/decr.do", req.getServletPath())) {
            String productId = req.getParameter("productId");
            if (null != productId) {
                LocalCache.decrCart(Long.valueOf(productId));
            }
            resp.sendRedirect("/cart/list.do");
        }
```

其中，decrCart 方法已经在`将商品添加到购物车中`时实现了，delCart 方法就是将缓冲中 cartMap 中的指定商品信息删除。

```java
    public static void delCart(Long productId) {
        cartMap.remove(productId);
    }
```

decrCart 需要判断减少一件商品后该商品数量是否为0，如果为0，直接将该商品删除。

```java
    public static void decrCart(Long productId) {
        boolean res = cartMap.get(productId).decrCount();
        if (res) {
            cartMap.remove(productId);
        }
    }
```

它调用 Cart 类中的 decrCount 方法，如果减少后数量为0，则返回 true。

```java
    public boolean decrCount() {
        count--;
        this.totalPrice = price * count;
        if (0 == count) {
            return true;
        }
        return false;
    }
```

最后不要忘记在 web.xml 为这三个功能注册

```xml
    ...
    <servlet-mapping>
        <servlet-name>CartServlet</servlet-name>
        <url-pattern>/cart/incr.do</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>CartServlet</servlet-name>
        <url-pattern>/cart/decr.do</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>CartServlet</servlet-name>
        <url-pattern>/cart/delete.do</url-pattern>
    </servlet-mapping>
```

购物车中商品的增加、减少和删除功能成功实现。

### 3.  购物车模拟结算

购物车结算功能就是在 CartServlet 的 service 方法中在增加一个选项。获取购物车中所有商品的集合，然后遍历集合将每个 cart 对象中计算好的 totalPrice 相加，再从缓存中删除掉遍历后的元素。

最后将购物车中所有商品的总价存入 request 对象，重定向到结算成功页面。

```java
 ...
 else if (Objects.equals("/cart/settlement.do", req.getServletPath())) {
            String[] cartIds = req.getParameterValues("carts");
            int totalPrice = 0;
            for (int i=0; i<cartIds.length; i++) {
                Cart cart = LocalCache.getCart(Long.valueOf(cartIds[i]));
                totalPrice += cart.getTotalPrice();
                LocalCache.delCart(cart.getId());
            }
            req.setAttribute("totalPrice", totalPrice);
            req.getRequestDispatcher("/WEB-INF/views/biz/settlement.jsp").forward(req, resp);
        }
```

其中 getCart 方法在 LocalCache 中定义，根据 cart 的 id 返回 cart 对象。

```java
    public static Cart getCart(Long id) {
        return cartMap.get(id);
    }
```

然后在 web.xml 中配置

```xml
    <servlet-mapping>
        <servlet-name>CartServlet</servlet-name>
        <url-pattern>/cart/settlement.do</url-pattern>
    </servlet-mapping>
```

结算成功页面

![TIM截图20180812193620.png](https://i.loli.net/2018/08/12/5b701bc5722c7.png)


##  收藏及浏览记录


### 1.  收藏及操作

其实这部分的功能跟之前比算是大同小异，收藏属于一个新的模块，所以要创建一个新的 servlet 处理收藏的处理。

```java

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
```

在 service 中可以看出又是那个功能，分别是

*   favorite.do：将商品添加到收藏夹
*   list.do：列举收藏夹中的商品
*   delete.do：将商品从收藏夹中移除

其中 service 各个功能的实现又调用了 LocalCache 中的方法，LocalCache 中新建一个 map 用以存储商品编号和商品。

```java
    private static Map<Long, Product> favoriteMap = new HashMap<>();
```

LocalCache 中关于收藏夹的方法

```java
    public static void addFavorite(Product product) {
        if (!favoriteMap.containsKey(product.getId())) {
            favoriteMap.put(product.getId(), product);
        }
    }

    public static void delFavorite(Product product) {
        if (favoriteMap.containsKey(product.getId())) {
            favoriteMap.remove(product.getId());
        }
    }

    public static List<Product> getFavorite() {
        return new ArrayList<>(favoriteMap.values());
    }
```

最后在 web.xml 中注册新的 servlet，这便是添加功能的一般步骤。

```xml
    <servlet>
        <servlet-name>FavoriteServlet</servlet-name>
        <servlet-class>me.seriouszyx.cart.servlet.FavoriteServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>FavoriteServlet</servlet-name>
        <url-pattern>/favorite/list.do</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>FavoriteServlet</servlet-name>
        <url-pattern>/favorite/favorite.do</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>FavoriteServlet</servlet-name>
        <url-pattern>/favorite/delete.do</url-pattern>
    </servlet-mapping>
```

收藏页面

![TIM截图20180812200001.png](https://i.loli.net/2018/08/12/5b7025ecbc2b2.png)

### 2.  浏览记录及操作

用户查看商品的详细信息，产生浏览记录，所以接下来我们要创建两个 servlet 分别处理详细信息和浏览记录的操作，然后通过 LocalCache 操作缓存，再配置 web.xml。

实现思路与之前的功能完全一致，所以这里只给出实现代码。

```java
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
```

```java
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
```

LocalCache.java

```java
    ...
    private static Map<Long, Product> browseLogMap = new HashMap<>();
    ...
    
    ...
    public static void addBrowserLog(Product product) {
        browseLogMap.put(product.getId(), product);
    }

    public static void delBrowserLog(Product product) {
        browseLogMap.remove(product.getId());
    }

    public static List<Product> getBrowseLogs() {
        return new ArrayList<>(browseLogMap.values());
    }
```

web.xml

```xml
    <servlet>
        <servlet-name>DetailInfoServlet</servlet-name>
        <servlet-class>me.seriouszyx.cart.servlet.DetailInfoServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>DetailInfoServlet</servlet-name>
        <url-pattern>/detail/detail.do</url-pattern>
    </servlet-mapping>


    <servlet>
        <servlet-name>BrowseServlet</servlet-name>
        <servlet-class>me.seriouszyx.cart.servlet.BrowseServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>BrowseServlet</servlet-name>
        <url-pattern>/browse/list.do</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>BrowseServlet</servlet-name>
        <url-pattern>/browse/delete.do</url-pattern>
    </servlet-mapping>
```

详细信息

![TIM截图20180812204841.png](https://i.loli.net/2018/08/12/5b702cc637d0d.png)

浏览记录

![TIM截图20180812204853.png](https://i.loli.net/2018/08/12/5b702cc6459b8.png)


##  分页及查询

### 1.  分页

假设显示主页面所有商品时，每次只显示12个商品，那么就需要用到分页技术，这里需要我们重新设计 ProductServlet 的 service 方法。

```java
@Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /** 获取当前页数 */
        String pageStr = req.getParameter("page");
        int page = 1;
        if (null != pageStr &&  !"".equals(pageStr)) {
            page = Integer.parseInt(pageStr);
        }

        List<Product> products = LocalCache.getProducts();
        int totalProducts = products.size();
        int totalPage = totalProducts % 12 > 0 ? totalProducts / 12 + 1: totalProducts / 12;

        req.setAttribute("curPage", page);
        req.setAttribute("prePage",page > 1 ? page - 1 : 1);
        req.setAttribute("nextPage", totalPage > page ? page + 1 : totalPage);
        req.setAttribute("totalPage", totalPage);

        req.setAttribute("products", LocalCache.getProducts(page, 12));
        req.getRequestDispatcher("/WEB-INF/views/biz/list.jsp").forward(req, resp);
    }
```

通过当前页数和总页数，可以计算出 curPage、prePage、nextPage、totalPage 四个分页显示的数据。

而且每次展示的 Product 对象的数量也发生变化，在 LocalCache 里实现一个新方法，根据当前页数和每页展示商品数返回商品对象的集合。

```java
    public static List<Product> getProducts(int page, int size) {
        List<Product> products = new ArrayList<>(productMap.values());
        int start = (page - 1) * size;
        int end = products.size() >= page * size ? page * size : products.size();
        return products.subList(start, end);
    }
```

这样主页面的商品就会分页显示

![TIM截图20180812210939.png](https://i.loli.net/2018/08/12/5b70319d33373.png)

### 2.  模糊查询

我们在 ProductServlet 的 service 中获取 查询课程的名称。

```java
    /** 搜索的课程名称 */
    String name = req.getParameter("title");
```

首先要获取查询之后的课程数量，因为同样要在首页分页显示，所以 name 为空时，应该返回所有的商品数量。

```java
    int totalProducts = LocalCache.getProductsCount(name);
```

LocalCache 中的 getProductsCount 方法，如果搜索栏为空，则返回所有商品数量，如果有值，就返回所查找的商品数量。

```java
    public static int getProductsCount(String name) {
        List<Product> products = new ArrayList<>();
        if (null != name && !"".equals(name)) {
            productMap.values().forEach(product -> {
                if (product.getName().contains(name)) {
                    products.add(product);
                }
            });
        } else {
            products.addAll(productMap.values());
        }
        return products.size();
    }
```

同样，向前端传输 products （需要展示的商品数量）时，也应该考虑 name 的值是否为空

```java
    req.setAttribute("products", LocalCache.getProducts(page, 12, name));
```

需要重新设计 LocalCache 中的 getProducts 方法。

```java
    public static List<Product> getProducts(int page, int size, String name) {
        List<Product> products = new ArrayList<>();
        if (null != name && !"".equals(name)) {
            productMap.values().forEach(product -> {
                if (product.getName().contains(name)) {
                    products.add(product);
                }
            });
        } else {
            products.addAll(productMap.values());
        }
        int start = (page - 1) * size;
        int end = products.size() >= page * size ? page * size : products.size();
        return products.subList(start, end);
    }
```

这样即可完成搜索功能。

![TIM截图20180812214000.png](https://i.loli.net/2018/08/12/5b7038b9dd8f3.png)