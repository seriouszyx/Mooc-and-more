package me.seriouszyx.listener;

import me.seriouszyx.utils.SqlSessionFactoryUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class IntSqlSessionLinstener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        // 初始化SqlSessionFactory对象
        SqlSessionFactoryUtils.initSqlSessionFactory();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // 关闭SqlSessionFactory对象
        SqlSessionFactoryUtils.close();
    }
}
