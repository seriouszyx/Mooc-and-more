package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.swing.*;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合，junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao"})
public class SeckillDaoTest {

    // 注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());      // 1000元秒杀iphonex
        System.out.println(seckill);                // Seckill{seckillId=1000, name='1000元秒杀iphonex', number=100, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
    }

    @Test
    public void queryAll() {
        /**
         * Caused by: org.apache.ibatis.binding.BindingException: Parameter 'offset' not found.
         *
         * List<Seckill> queryAll(int offset, int limit);
         *
         * java 没有保存形参的记录:
         *  List<Seckill> queryAll(int offset, int limit); -> queryAll(arg0, arg1)
         */
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }

        /**
         * Seckill{seckillId=1000, name='1000元秒杀iphonex', number=100, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
         * Seckill{seckillId=1001, name='500元秒杀ipad2', number=200, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
         * Seckill{seckillId=1002, name='300元秒杀小米4', number=300, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
         * Seckill{seckillId=1003, name='200元秒杀红米note', number=400, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
         */
    }


    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000l, killTime);
        System.out.println("updateCount=" + updateCount);       // updateCount=1
    }

}