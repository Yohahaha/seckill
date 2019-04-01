package yoha.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import yoha.seckill.dao.SeckillDao;
import yoha.seckill.entity.Seckill;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;
    @Test
    public void getSeckill() {
        Seckill seckill = redisDao.getSeckill(1000L);
        System.out.println(seckill);
    }

    @Test
    public void putSeckill() {
        long id = 1001L;
        Seckill seckill = seckillDao.queryById(id);
        String result = redisDao.putSeckill(seckill);
        System.out.println(result);
    }
}