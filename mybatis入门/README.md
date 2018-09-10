#   MyBatis 入门

##  mybatis 

创建一个 Maven 工程，增加项目所需 pom 依赖。

```xml
<dependencies>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.11</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>RELEASE</version>
            <scope>test</scope>
        </dependency>
</dependencies>
```

下面完成一个简单的操作数据库的例子，首先创建数据库及表。

```sql
CREATE DATABASE mytest;

USE mytest;

CREATE TABLE users (
		id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户编号',
		username VARCHAR(50) COMMENT '登录账号',
		userpass VARCHAR(50) COMMENT '登录密码',
		nickname VARCHAR(20) COMMENT '用户昵称'
);

INSERT INTO users VALUES (1, 'admin', 'admin', '管理员');
INSERT INTO users VALUES (2, 'manager', 'manager', '管理员');
INSERT INTO users VALUES (3, 'administrator', 'administrator', '管理员');

```

然后创建 users 表的对应类 Users，并为它添加 get/set 和 toString 方法。

```java
public class Users {

    private Integer id;
    private String username;
    private String userpass;
    private String nickname;
    
    ...
}
```

在 resources 下新建 `mybatis.xml` 文件，配置数据库。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mytest?serverTimezone=GMT&amp;useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="zhaoyixiang123"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <!--<mapper resource="org/mybatis/example/BlogMapper.xml"/>-->
    </mappers>
</configuration>
```

其中 `dataSource` 的 `properties` 中修改适应的配置。

再在 `resources` 下创建 `mapper` 文件夹，里面创建 `usersMapper.xml` 用于 Users 表的数据处理。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace 命名空间-->
    <!--通常情况下，命名空间的值，就是当前操作实体类的全名称-->
<mapper namespace="me.seriouszyx.app.Users">
    <select id="usersList" resultType="me.seriouszyx.app.Users">
        select * from users
    </select>
</mapper>
```

其中，`namespace` 和 `resultType` 为 Users 类的路径，`id` 为该条 sql 语句的标识。

然后在 mybatis.xml 中添加映射。

```xml
    ...
    <mappers>
        <!--<mapper resource="org/mybatis/example/BlogMapper.xml"/>-->
        <mapper resource="mapper/usersMapper.xml" />
    </mappers>
    ...
```

下面在 test/java 下创建一个测试类，用以测试写的 sql 语句是否正确。

```java
public class testDemo {

    @Test
    public void testDemo1() throws IOException {
        // 初始化mybatis配置环境
        String resource = "mybatis.xml";
        InputStream is = Resources.getResourceAsStream(resource);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);

        // 打开和数据库之间的会话
        SqlSession session = factory.openSession();

        // 进行数据处理
        List<Users> usersList = session.selectList("usersList");

        for (Users user : usersList
             ) {
            System.out.println(user);
        }

        // 关闭资源
        session.close();
    }
}
```

运行测试方法，控制台输出正确结果。

![TIM截图20180909111938.png](https://i.loli.net/2018/09/09/5b949153bcd9c.png)

下面是 mybatis 使用的一般流程。

![TIM截图20180909112044.png](https://i.loli.net/2018/09/09/5b949195bd465.png)


