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

完整的 [pom.xml]()