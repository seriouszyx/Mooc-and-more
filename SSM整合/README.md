#  SSM 整合案例 —— Java 高并发秒杀 API 

##  说明

本文根据慕课网 [Java 高并发秒杀系列](https://www.imooc.com/u/2145618/courses?sort=publish&skill_id=220)整理而成，在于加深初学者对 SSM 三大框架整合的理解，项目源码已发布在我的 [GitHub](https://github.com/seriouszyx/Mooc-and-more/tree/master/SSM%E6%95%B4%E5%90%88)，如果对你有帮助的话，请给一个 **star**。

##  项目创建

项目使用 maven 创建，3.5 以上版本丢弃 create 改用 generate 创建。

```text
mvn archetype:generate -DgroupId=org.seckill -DartifactId=seckill -DarchetypeArtifactId=maven-archetype-webapp
```  

maven 创建的模版 web.xml 是 2.3 版本，默认不支持 jstl 表达式，可以换成 3.1 的头。

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                      http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1"
         metadata-complete="true">

    <!--修改servlet版本为 3.1-->
    
</web-app>
```

使用 maven 添加依赖，分为以下几方面：

*   测试 —— junit 4
*   日志 —— slf4j + logback
*   数据库相关依赖 —— jdbc、c3p0、mybatis、mybatis-spring
*   servlet web 相关依赖 —— standard、jstl、jackson（core+databind+annotations）、javax.servlet-api
*   Spring 依赖
    *   Spring 核心依赖 —— spring-core、spring-beans、spring-context
    *   Spring Dao 层依赖 —— spring-jdbc、spring-tx
    *   Spring web 相关依赖 —— spring-web、spring-webmvc
    *   Spring test 相关依赖 —— spring-test

>   完整的 [pom.xml](https://github.com/seriouszyx/Mooc-and-more/blob/master/SSM%E6%95%B4%E5%90%88/seckill/pom.xml)

##  Dao 层

###  数据库表设计

```sql
create database seckill;
--使用数据库
use seckill;
```

```sql
--创建秒杀数据表
CREATE TABLE seckill(
  `seckill_id` BIGINT NOT NUll AUTO_INCREMENT COMMENT '商品库存ID',
  `name` VARCHAR(120) NOT NULL COMMENT '商品名称',
  `number` int NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP  NOT NULL COMMENT '秒杀开始时间',
  `end_time`   TIMESTAMP   NOT NULL COMMENT '秒杀结束时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  key idx_start_time(start_time),
  key idx_end_time(end_time),
  key idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';
```

```sql
-- 秒杀成功明细表
-- 用户登录认证相关信息(简化为手机号)
CREATE TABLE success_killed(
  `seckill_id` BIGINT NOT NULL COMMENT '秒杀商品ID',
  `user_phone` BIGINT NOT NULL COMMENT '用户手机号',
  `state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  `create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY(seckill_id,user_phone),/*联合主键*/
  KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表'
```

>   完整的 [schema.sql](https://github.com/seriouszyx/Mooc-and-more/blob/master/SSM%E6%95%B4%E5%90%88/seckill/src/main/sql/schema.sql)

###  Dao 层编码

根据数据库表的字段名在 entity 包下创建相应的实体类 Seckill, SunccessKilled。

接着在 Dao 层设计接口 SeckillDao, SuccessKilledDao。

```java
public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1，标示更新的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

}
```

```java
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return  插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
```

Dao 层接口的实现交给 mybatis 处理，一般选择使用 mapper 映射实现。为了方便数据库中表字段和 entity 下实体类相对应，在 resources 下配置 mybatis-config.xml。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置全局属性 -->
    <settings>
        <!-- 使用jdbc的getGenratedKeys 获取数据库的自增主键值 -->
        <setting name="useGeneratedKeys" value="true"/>
        <!-- 使用列别名替换列名 默认：true -->
        <setting name="useColumnLabel" value="true" />
        <!-- 开启驼峰命名转换：Table(create_time) -> Entity(createTime) -->
        <setting name="mapUnderscoreToCamelCase" value="true" />
    </settings>
</configuration>
```

接下来在 mapper 中手写 sql，完成 Dao 层接口的实现。

>   完整 [mapper](https://github.com/seriouszyx/Mooc-and-more/tree/master/SSM%E6%95%B4%E5%90%88/seckill/src/main/resources/mapper)

###  mybatis 整合 Spring

首先编写数据库配置文件，相关配置项自行修改。

```xml
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://127.0.0.1:3306/seckill?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=CONVERT_TO_NULL
username=root
password=root
```

创建 spring-dao 文件，分为以下几方面配置

*   配置数据库相关参数
*   配置数据库连接池
    *   配置连接池属性
    *   c3p0 私有属性
*   配置 SqlSessionFactory 对象
*   配置扫描 dao 接口包，动态实现 dao 接口，注入带 spring 的容器中

>   完整的 [spring-dao](https://github.com/seriouszyx/Mooc-and-more/blob/master/SSM%E6%95%B4%E5%90%88/seckill/src/main/resources/spring/spring-dao)

###  Dao 层单元测试

为 Dao 层编写单元测试，注意为单元测试类标识配置文件。

```java
/**
 * 配置spring和junit整合，junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao"})
```

>   完整 [Dao 单元测试](https://github.com/seriouszyx/Mooc-and-more/tree/master/SSM%E6%95%B4%E5%90%88/seckill/src/test/java/org/seckill/dao)

## Service 层

### 接口设计

在设计 Service 层接口时，最核心的一个思想就是“站在使用者的角度”，而不是单纯为了封装 Dao 层方法，以使用者的眼光来看待，对 Dao 层代码进行封装和补充，才是设计 Service 接口最应该做的事。

同时，设计接口的过程中，还要着重注意`方法定义粒度`，`方法参数`和`方法返回类型`三方面，这样才能设计出易用健壮的接口。

```java
public interface SeckillService {

    /**
     * 查询所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时，输出秒杀接口地址，否则输出系统时间和秒杀时间
     * @param sekillId
     */
    Exposer exportSeckillUrl(long sekillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillExcption, RepeatKillException, SeckillCloseException;

}
```

从上述代码可以看出，异常的处理方式有些不同，一般建议一个新的 Exception 包，将事务处理过程中要处理的异常都集中放在该包下，以达到后台代码的模块化。

```java
/**
 *  秒杀相关业务异常
 */
public class SeckillExcption extends RuntimeException {

    public SeckillExcption(String message) {
        super(message);
    }

    public SeckillExcption(String message, Throwable cause) {
        super(message, cause);
    }
}
```

先定义一个总的异常类继承 RuntimeException，把期间的所有异常都转换成运行期异常，因为这样才能将他们交由 Spring 的事务管理器处理（Spring 事务管理器不处理编译期异常）。

```java
/**
 *  重复秒杀异常（运行期异常）
 */
public class RepeatKillException extends SeckillExcption {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }

}
```

```java
/**
 *  秒杀关闭异常
 */
public class SeckillCloseException extends SeckillExcption {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 接口实现

 Service 层的接口实现主要考虑事务管理器的使用， 一种错误的理解就是所有的方法都使用事务管理器。
 
 *  开发团队达成一致约定，明确标注事务方法的编程风格
 *  保证事务方法的执行时间尽可能短，不要穿插其他网路操作，RPC/HTTP 请求或者剥离到事务方法外部
 *  不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制

 就本例而言，只有秒杀执行事务需要使用事务管理器，其他方法就像普通的 Service 代码实现就好。

 ```java
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillExcption, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillExcption("seckill data rewrite");
        }
        // 执行秒杀逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();
        try {
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                // 没有更新到记录,秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    // 重复秒杀
                    throw new RepeatKillException("seckill repeted");
                } else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 编译期异常转化为运行期异常
            throw new SeckillExcption("seckill inner error: " + e.getMessage());
        }
    }
 ```
 
 完整的 [Service 层代码](https://github.com/seriouszyx/Mooc-and-more/tree/master/SSM%E6%95%B4%E5%90%88/seckill/src/main/java/org/seckill/service)
 
 ###    使用 Spring 托管 Service 依赖
 
 与 Dao 层 配置相比，Service 层配置则显得很轻松。
 
 ```xml
 <?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 扫描service包下所有使用注解的类型 -->
    <context:component-scan base-package="org.seckill.service"/>


    <!-- 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!-- 注入数据库连接池 -->
        <property name="dataSource" ref="dataSource"/>
    </bean>
    <!-- 配置基于注解的声明式事务
        默认使用注解来管理事务行为
     -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
 ```
 
 注意，在 Service 层的单元测试之前，最好将日志配置好。
 
 ```xml
 <?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are  by default assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
 ```
 
 ###    Service 层单元测试
 
 单元测试要灵活利用日志以打印输出信息。
 
 ```java
     @Test
    public void testSeckillLogic() {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);

            long phone = 13502192128l;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}", seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        } else {
            // 秒杀未开启
            logger.warn("exposer={}", exposer);
        }
    }
```
 
 Service 层单元测试[完整代码](https://github.com/seriouszyx/Mooc-and-more/blob/master/SSM%E6%95%B4%E5%90%88/seckill/src/test/java/org/seckill/service/SeckillServiceTest.java)
 
 ## Web 层
 
 就对于初学者的我来讲，强大的 Spring MVC 带给我的冲击是最明显的，它让 View 层和 Control 层的交互是如此的可靠便捷。
 
 ###    Restful 接口
 
 [Restful URL](http://www.ruanyifeng.com/blog/2014/05/restful_api.html) 已经是现在 web 开发的潮流了，关于这方面不想做太多的描述，下面给出本项目设计的 URL：
 
 
 ![](http://pi0evhi68.bkt.clouddn.com/15431503742470.jpg)
 
 ###    Spring MVC 整合 Spring
 
 Spring MVC 配置主要分为以下几步：
 
 *  开启 Spring MVC 注解模式
 *  静态资源默认 servlet 配置
 *  配置 jsp，显示 ViewResolver
 *  扫描 web 相关的 bean

 ```xml
 <?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    <!-- 配置springMVC -->

    <!-- 1:开启SpringMVC注解模式 -->
    <!-- 简化配置:
        (1)自动注册DefaultAnnotationHandleMapping,AnnotationMethodHandlerAdapter
        (2)默认提供了一系列:数据绑定,数字和日期的format @NumberFormat,@DateTimeFormat
            xml,json默认读写支持
        -->
    <mvc:annotation-driven/>
    <!-- Servlet-Mapping 映射路径:"/" -->
    <!-- 2:静态资源默认servlet配置
       1:加入对静态资源的处理
       2:允许使用/做映射
       -->
    <mvc:default-servlet-handler/>

    <!-- 3:配置jsp 显示ViewResolver -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <!-- 4:扫描web相关的bean -->
    <context:component-scan base-package="org.seckill.web"/>
</beans>
 ```
 
 ###    dto 数据封装
 
 为了使 json 数据交互更加易读易用，在 View 与 Control 之间有进行了一次数据封装。
 
 ```java
 // 所有 ajax 请求的返回类型，封装 json 结果
public class SeckillResult<T> {

     private boolean success;

     private T data;

     private String error;
     
     ...
}
 ```
 
 主要的三个成员变量，封装了 json 数据交互的关键，success 代表一次请求服务端的处理是否成功，如果报错，则 error 携带错误信息，范型设计 data 利于其他封装的数据使用该类进行数据传输。
 
 
 ```java
/**
 *  暴露秒杀地址 DTO
 */
public class Exposer {

    // 是否开启秒杀
    private boolean exposed;

    private String md5;

    private long seckillId;

    // 系统当前时间（毫秒）
    private long now;

    private long start;

    private long end;
    
    ...
}
 ```


```java
/**
 *  封装秒杀执行后的结果
 */
public class SeckillExecution {

    private long seckillId;

    // 秒杀执行结果状态
    private int state;

    // 状态表示
    private String stateInfo;

    // 秒杀成功对象
    private SuccessKilled successKilled;
    
    ...
}
```

将前端需要的信息进一步封装，有利于数据传输。

其中，为了异常信息更规范的表示，使用枚举类对可能发生的异常信息进行封装。

异常枚举类[完整代码](https://github.com/seriouszyx/Mooc-and-more/blob/master/SSM%E6%95%B4%E5%90%88/seckill/src/main/java/org/seckill/enums/SeckillStatEnum.java)

### controller 实现

Controller 与前端交互主要有两种处理手段：

*   一种是链接的跳转/重定向
*   另一种是 json 数据的交互

下面分别以两个方法举例子

```java
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // 获取列表页
        List<Seckill> list=  seckillService.getSeckillList();
        model.addAttribute("list", list);
        // list.jsp + model = ModelAndView

        return "list"; //WEB_INF/jsp/list.jsp
    }
```

```java
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) {
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e1) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (SeckillCloseException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
    }
```

Controller 类[完整代码](https://github.com/seriouszyx/Mooc-and-more/blob/master/SSM%E6%95%B4%E5%90%88/seckill/src/main/java/org/seckill/web/SeckillController.java)

##  总结

至此，所有有关后台的代码已整理完毕，我们已经完成了相对健壮的接口，对于前台的实现不太想描述，也不是我写这篇文章的重点，如果有需要，可以 download 源码读。

本篇文章在于讲解基于 SSM 框架的 web 应用的开发流程，重在强调各个配置文件以及设计思想，希望可以使零散的三大框架的知识紧凑起来，让初学者明白三者相互影响，不可分割。















