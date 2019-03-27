package yoha.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import yoha.seckill.entity.SuccessKilled;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Autowired
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        /**
         * 第一次插入时返回值为1
         * 第二次插入时返回值为0
         * 这是因为在mapper文件中设置了ignore关键字，当发生主键冲突时，不会报错，而是不插入，且返回0
         */
        long seckillId = 1001L;
        long userPhone = 13311223344L;
        int killed = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        System.out.println(killed);
    }

    @Test
    public void queryByIdWithSeckill() {
//        当查询的seckillId和seckill表中的Id列对应时，会自动绑定seckill对象到successKilled中的seckill实体中
//        如果两者的seckillId不一样，则查询的结果为null
        long seckillId = 1001L;
        long userPhone = 13311223344L;
        SuccessKilled killed = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
        System.out.println(killed);
    }
}