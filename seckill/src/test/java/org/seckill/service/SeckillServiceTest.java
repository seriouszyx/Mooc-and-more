package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao",
        "classpath:spring/spring-service"
        })
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
        /**
         * 16:36:54.772 [main] DEBUG org.seckill.dao.SeckillDao.queryAll - ==>  Preparing: select seckill_id, name, number, start_time, end_time, CREATE_TIME from seckill order by create_time desc limit ?, ?
         * 16:36:54.806 [main] DEBUG org.seckill.dao.SeckillDao.queryAll - ==> Parameters: 0(Integer), 4(Integer)
         * 16:36:54.837 [main] DEBUG org.seckill.dao.SeckillDao.queryAll - <==      Total: 4
         * 16:36:54.844 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession
         *
         * list=[
         *      Seckill{seckillId=1000, name='1000元秒杀iphonex', number=99, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018},
         *      Seckill{seckillId=1001, name='500元秒杀ipad2', number=200, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018},
         *      Seckill{seckillId=1002, name='300元秒杀小米4', number=300, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018},
         *      Seckill{seckillId=1003, name='200元秒杀红米note', number=400, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
         *      ]
         */
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
        /**
         * 16:30:12.325 [main] DEBUG org.seckill.dao.SeckillDao.queryById - ==>  Preparing: select seckill_id, name, number, start_time, end_time, CREATE_TIME from seckill where seckill_id = ?
         * 16:30:12.359 [main] DEBUG org.seckill.dao.SeckillDao.queryById - ==> Parameters: 1000(Long)
         * 16:30:12.429 [main] DEBUG org.seckill.dao.SeckillDao.queryById - <==      Total: 1
         * 16:30:12.435 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession
         *
         * seckill=Seckill{seckillId=1000, name='1000元秒杀iphonex', number=99, startTime=Sat Nov 17 08:00:00 CST 2018, endTime=Mon Nov 19 08:00:00 CST 2018, createTime=Sun Nov 18 02:18:43 CST 2018}
         */
    }

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


}