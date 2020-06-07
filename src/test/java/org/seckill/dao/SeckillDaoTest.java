package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和Junit整合，Junit启动时加载SpringIOC容器
 * spring-test，junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    // 注入Dao实现类的依赖
    // 这里改为@Autowired后，SeckillServiceTest中的测试会报错  原因不知... //TODO
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("updateCount = "+updateCount);
    }

    @Test
    public void qureyById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.qureyById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void qureyAll() {
        //Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
        // java没有保存形参的记录:queryAll(int offset, int limit) -> queryAll(arg1,arg2)
        List<Seckill> seckills = seckillDao.qureyAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }

    }
}