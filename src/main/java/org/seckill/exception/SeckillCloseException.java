package org.seckill.exception;

/**
 * 秒杀关闭异常
 * @author yu
 * @date 2020/6/6 16:09
 */
public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
