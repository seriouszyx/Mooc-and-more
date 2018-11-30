package org.seckill.exception;

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
