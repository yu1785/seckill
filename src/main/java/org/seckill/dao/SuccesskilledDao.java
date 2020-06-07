package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccesskilledDao {

    /**
     * 插入购买明细，可重复过滤
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     *  根据id查询SuccessKilled并携带秒杀产品对象实体
     *  由于一个秒杀单 可以有多个人抢，所以传入参数不能只是 seckillId
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled qureyByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

}
