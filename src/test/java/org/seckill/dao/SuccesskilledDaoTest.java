package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccesskilledDaoTest {

    // 注入Dao实现类的依赖
    // 这里改为@Autowired后，SeckillServiceTest中的测试会报错  原因不知... //TODO
    @Resource
    private SuccesskilledDao successkilledDao;

    @Test
    public void insertSuccessKilled() {
        /**
         * 第一次:insertCount=1
         * 第二次:insertCount=0
         * 重复插入相同字段 无效
         */
        long id = 1001L;
        long phone = 12345678911L;
        int insertCount = successkilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount = "+insertCount);
    }

    @Test
    public void qureyByIdWithSeckill() {
        long id = 1001L;
        long phone = 12345678911L;
        SuccessKilled successKilled = successkilledDao.qureyByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}