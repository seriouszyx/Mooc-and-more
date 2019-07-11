package top.seriouszyx.diveinspringboot.bootstrap;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import top.seriouszyx.diveinspringboot.repository.MyFirstLevelRepository;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/11 20:20
 * @description：仓储的引导类
 */
@ComponentScan(basePackages = "top.seriouszyx.diveinspringboot.repository")
public class RepositoryBootstrap {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(RepositoryBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        // myFirstLevelRepository Bean 是否存在
        MyFirstLevelRepository myFirstLevelRepository =
                context.getBean("myFirstLevelRepository", MyFirstLevelRepository.class);

        System.out.println("myFirstLevelRepository Bean ： " + myFirstLevelRepository);

        // 关闭上下文
        context.close();
    }

}
