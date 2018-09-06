#   JDBC 入门

##  JDBC 概述

Java数据库连接，（Java Database Connectivity，简称JDBC）是Java语言中用来规范客户端程序如何来访问数据库的应用程序接口，提供了诸如查询和更新数据库中数据的方法。

没有 JDBC 的时候，如果 Java 程序员想连接 mysql 数据库，就需要了解 mysql 的驱动，如果想连接 Oracle 数据库，就要了解 Oracle 的驱动方法。

![TIM截图20180813105849.png](https://i.loli.net/2018/09/03/5b8d14c4166d6.png)

为了减少不必要的麻烦，SUN 公司开发了 JDBC 标准，Java 程序员只需要了解 JDBC 的接口规范，而且每个数据库的驱动都需要实现 JDBC 的规范。在开发过程中，只需要调用规范中的方法，就可以连接不同类型的数据库。

![maxresdefault.jpg](https://i.loli.net/2018/09/03/5b8d14c4135c1.jpg)

## JDBC 使用步骤

先创建数据库及表，完成准备工作

```sql
create database jdbctest;
use jdbctest;
create table user (
	uid int primary key auto_increment,
	username varchar(20),
	password varchar(20),
	name varchar(20)
);

insert into user values (null, 'aaa', '111', '张三');
insert into user values (null, 'bbb', '222', '李四');
insert into user values (null, 'ccc', '333', '王五');
```

数据插入成功

```sql
mysql> select * from user;
+-----+----------+----------+--------+
| uid | username | password | name   |
+-----+----------+----------+--------+
|   1 | aaa      | 111      | 张三   |
|   2 | bbb      | 222      | 李四   |
|   3 | ccc      | 333      | 王五   |
+-----+----------+----------+--------+
3 rows in set (0.00 sec)
```

将`资料`中的 jar 包导入工程。

然后创建 Java 项目，输入以下代码（注意：新版本的jdbc与老版本有略微的区别）

```java
/** jdbc 入门程序 */
    @Test
    public void demo1() {

        try {
            /** 加载驱动 */
            DriverManager.registerDriver(new Driver());

            /** 获得连接 */
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbctest?serverTimezone=GMT&useSSL=false", "root", "zhaoyixiang123");

            /** 创建SQL语句的对象 */
            String sql = "select * from user";
            Statement statement = conn.createStatement();

            /**并且执行SQL语句 */
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int uid = resultSet.getInt("uid");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");

                System.out.println(uid + " " +username + " " + password + " " + name);
            }

            /** 释放资源 */
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
```

运行程序，得到查询结果

![TIM截图20180905122600.png](https://i.loli.net/2018/09/05/5b8f5af95423c.png)

下面解释一下用到的几个核心类

*   DriverManager 驱动管理类
    -   注册驱动
        >   老版本需要 `Class.forName("com.mysql.jdbc.Driver");` 注册驱动，新版本自动注册。
    -   获得连接

        >   Connection getConnection(String url, String username, String password);<br>
        url 写法：`jdbc:mysql://localhost:3306/jdbctest?serverTimezone=GMT&useSSL=false`
*   Connection 连接对象
    -   创建执行 sql 语句的对象
    >   Statement createStatement() ：执行 sql 语句，有 sql 注入的漏洞存在 <br>
        PreparedStatement prepareStatement(String sql) ：预编译 sql 语句，解决 sql 注入的漏洞<br>
        CallableStatement prepareCall(String sql) ：执行 sql 中的存储过程
    
    -   进行事务的管理
    >   setAutoCommit(boolean autoCommit) ：设置事务自动提交 <br>
        commit() ：事务提交 <br>
        rollback() ：事务回滚
*   Statement 执行 sql
    -   执行 sql 语句
    >   boolean execute(String sql) ：执行 sql,执行 select 语句返回 true，否则返回 false <br>
        ResultSet executeQuery(String sql) ：执行 sql 中的 select 语句<br>
        int executeUpdate(String sql) ：执行 sql 中的insert/update/delete 语句，返回影响行数
        
    - 执行批处理操作
    >   addBatch(String sql) ：添加到批处理<br>
        executeBatch(String sql) ：执行批处理<br>
        clearBatch() ：删除批处理
        
* ResultSet 结果集
    -   获取查询到的结果
    >   next() ：是否有下一行记录 <br>
        针对不同类型的数据可以使用 getXXX() 获取数据

        
JDBC 程序完成后，切记要释放程序在运行过程中，创建的那些与数据库交互的对象，这些对象通常是 ResultSet,Statement 和 Connection 对象。

下面是简单的 JDBC 程序的执行过程。
    
![JDBC_Cycle.png](https://i.loli.net/2018/09/05/5b8f592b95576.png)


##  JDBC 资源释放

如果在释放资源之前发生异常，那么资源就没有被释放，所以我们需要优化资源的释放。

```java
/** JDBC 资源的释放 */
    @Test
    public void demo1() {

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            /** 加载驱动 */
            DriverManager.registerDriver(new Driver());

            /** 获得连接 */
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbctest?serverTimezone=GMT&useSSL=false", "root", "zhaoyixiang123");

            /** 创建SQL语句的对象 */
            String sql = "select * from user";
            statement = conn.createStatement();

            /**并且执行SQL语句 */
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int uid = resultSet.getInt("uid");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");

                System.out.println(uid + " " +username + " " + password + " " + name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            /** 释放资源 */
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {

                }
                resultSet = null; // 垃圾回收机制更早回收对象
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {

                }
                statement = null;
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {

                }
                conn = null;
            }
        }
    }
```

finally 中释放资源可以确保发生异常也能进行资源的释放。

##  JDBC 工具类的抽取

为了避免每次写 JDBC 程序都出现大量重复代码，我们将抽取 JDBC 工具类。

首先，为了灵活的实用工具类，我们创建配置文件 `jdbc.properties` ，将所需配置写入，方便修改。

```text
url=jdbc:mysql://localhost:3306/jdbctest?serverTimezone=GMT&useSSL=false
username=root
password=zhaoyixiang123
```

然后创建 JDBC 工具类，主要提供了获得`连接对象`和`释放资源`的方法。

```java
public class JDBCUtils {

    private static final String url;
    private static final String username;
    private static final String password;

    static {
        // 加载属性文件并解析
        Properties properties = new Properties();
        InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    /**
     *  注册驱动的方法
     **/
    public static void loadDriver() throws SQLException {
        DriverManager.registerDriver(new Driver());
    }

    /**
     *  获得连接的方法
     * */
    public static Connection getConnection() throws SQLException {
        loadDriver();
        return DriverManager.getConnection(url, username, password);
    }

    /**
     *  资源释放
     * */
    public static void release(Statement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            statement = null;
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }

    public static void release(ResultSet resultSet, Statement statement, Connection connection) {
        release(statement, connection);
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resultSet = null;
        }
    }
}
```

测试工具类，数据保存成功。

```java
public class JDBCDemo3 {
    /** 保存记录 */
    @Test
    public void save() {
        Connection conn = null;
        Statement statement = null;
        try {
            // 获得连接
            conn = JDBCUtils.getConnection();
            // 创建执行 SQL 语句的对象
            statement = conn.createStatement();
            // 编写 SQL
            String sql = "insert into user values (null, 'fff', '123', '小吴')";
            // 执行 SQL
            int num = statement.executeUpdate(sql);

            if (num > 0) {
                System.out.println("保存成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(statement, conn);
        }
    }
}

```

##  防止 SQL 注入

为了防止 SQL 注入，我们将使用 `PreparedStatement` 类来取代 `Statement` 类进行数据的 CRUD。

## 数据库连接池

`连接池`是创建和管理一个连接的缓冲池的技术，这些连接准备好被任何需要它们的`线程`使用。

应用程序直接获得连接的`缺点`：用户每次请求都需要向数据库获得连接，而数据库创建连接通常需要消耗相对较大的资源，创建时间也比较长。假设网站一天十万的访问量，数据库服务器就需要十万次连接，极大的浪费数据库资源，并且极易`造成数据库服务器内存的溢出`。

##  c3p0 连接池

