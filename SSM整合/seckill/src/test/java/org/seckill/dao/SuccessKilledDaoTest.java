package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        long id = 1000l;
        long phone = 12502181181l;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount=" + insertCount);
        /**
         * 第一次：insertCount=1
         * 第二次：insertCount=0
         */
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1000l;
        long phone = 12502181181l;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
        /**
         * SuccessKilled{seckillId=1000, userPhone=12502181181, state=-1, createTime=null}
         * Seckill{seckillId=1000, name='1000元秒杀iphonex', number=99, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
         */
    }
}