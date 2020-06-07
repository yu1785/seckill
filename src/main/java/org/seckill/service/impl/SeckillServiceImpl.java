package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccesskilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * @author yu
 * @date 2020/6/6 16:16
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    //日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccesskilledDao successkilledDao;

    //md5盐值字符串，用于混淆 MD5
    private final String slat = "asdf#￥%gh12QWE34dldngskbk7998";

    @Override
    public List<Seckill> getSeckillList() {
        List<Seckill> seckills = seckillDao.qureyAll(0, 4);
        return seckills;
    }

    @Override
    public Seckill getById(long seckillId) {
        Seckill seckill = seckillDao.qureyById(seckillId);
        return seckill;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.qureyById(seckillId);
        if (seckill == null){
            return new Exposer(false,seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        //现在时间不在秒杀时间范围内
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),
                    startTime.getTime(),endTime.getTime());
        }
        // 转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    public String getMD5(long seckillId){
        String base = seckillId+"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点：
     *      1.开发团队达成一致约定，明确标注事务方法的编程风格
     *      2.保证事务方法的执行时间尽量短，不要穿插其他网络操作 RPC/HTTP请求或者剥离到事务方法外部
     *      3.不是所有的方法搜都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */
    public SeckillExecution executeScekill(long seckillId, long userPone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrited");
        }
        //执行秒杀逻辑： 减库存+记录购买行为
        Date nowTime = new Date();

        try {
            //减库存
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0){
                throw  new SeckillCloseException("seckill is closed");
            } else {
                //记录购买行为
                int insertCount = successkilledDao.insertSuccessKilled(seckillId, userPone);
                if (insertCount <= 0){
                    //重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilled successKilled = successkilledDao.qureyByIdWithSeckill(seckillId, userPone);
//                    return new SeckillExecution(seckillId,1,"秒杀成功",successKilled);
                    //使用枚举 SeckillStateEnum.SUCCESS
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        } catch (SeckillCloseException e1){
            throw e1;
        } catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            //所有编译器异常转化为运行期异常
            throw new SeckillException("seckill inner error: "+e.getMessage());
        }
    }
}
