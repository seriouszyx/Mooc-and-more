package me.seriouszyx.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName SqlSessionFactoryUtils
 * @Description TODO
 * @Author Yixiang Zhao
 * @Date 2018/9/9 14:17
 * @Version 1.0
 */
public class SqlSessionFactoryUtils {
    private static String RESOURCE = "mybatis-config.xml";

    private static SqlSessionFactory sqlSessionFactory;

    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<SqlSession>();

    /**
     * @Author Yixiang Zhao
     * @Description 初始化SqlSessionFactory
     * @Date 14:24 2018/9/9
     * @Param []
     * @return void
     **/
    public static void initSqlSessionFactory() {
        try {
            InputStream is = Resources.getResourceAsStream(RESOURCE);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author Yixiang Zhao
     * @Description 获取工厂对象
     * @Date 14:22 2018/9/9
     * @Param []
     * @return org.apache.ibatis.session.SqlSessionFactory
     **/
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 关闭SqlSession
     * @Date 14:24 2018/9/9
     * @Param []
     * @return void
     **/
    public static void close() {
        SqlSession session = threadLocal.get();
        if (session != null) {
            session.close();
            threadLocal.set(null);
        }
    }


}
