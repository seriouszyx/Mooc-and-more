#  SSM 整合案例 —— Java 高并发秒杀 API 

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

### 






