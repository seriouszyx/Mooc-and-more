package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillExcption;

import java.util.List;

/**
 *  站在"使用者"的角度设计接口
 *  三个方面：方法定义粒度，参数，返回类型(return 类型/异常)
 *
 */
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
