package yoha.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import yoha.seckill.dto.Exposer;
import yoha.seckill.dto.SeckillExecution;
import yoha.seckill.entity.Seckill;
import yoha.seckill.exception.RepeatKillException;
import yoha.seckill.exception.SeckillCloseException;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getSeckillById() {
        long seckillId = 1000L;
        Seckill seckill = seckillService.getSeckillById(seckillId);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long seckillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        logger.info("exposer={}",exposer);
        // 第一条记录是当秒杀未开启或已经结束时的返回结果，可以看到里面没有md5秒杀地址
        // 第二条记录是开启秒杀时返回的记录，给出了秒杀地址
        //exposer=Exposer{exposed=false, md5='null', seckillId=1000, now=1553743630344, start=1451577600000, end=1451664000000}
        //exposer=Exposer{exposed=true, md5='927dd46ff9b658752420d8f569bc4840', seckillId=1000, now=0, start=0, end=0}
    }

    @Test
    public void executeSeckill() {
        long id = 1000;
        long userPhone = 12211223323L;
        String md5 = "927dd46ff9b658752420d8f569bc4840";
        SeckillExecution result = seckillService.executeSeckill(id, userPhone, md5);
        logger.info("result={}",result);
    }

    @Test
    public void TestSeckillLogic(){
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()){
            long userPhone = 13321214433L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution result = seckillService.executeSeckill(id, userPhone, md5);
                logger.info("result={}",result);
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }
        }else {
            logger.warn("exposer={}",exposer);
        }
        // 第一次成功秒杀的结果
        //result=SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功',
        //      successKilled=SuccessKilled{userPhone=13321214433, state=0, createTime=Thu Mar 28 11:47:31 CST 2019, seckill=1000元秒杀iphone6}}

        // 第二次重复秒杀抛出异常
        //ERROR y.seckill.service.SeckillServiceTest - seckill repeat
    }

    @Test
    public void TestProcedure(){
        long id = 1000L;
        long userPhone = 13344228822L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(id, userPhone, md5);
            logger.info(execution.getStateInfo());
        }
    }
}