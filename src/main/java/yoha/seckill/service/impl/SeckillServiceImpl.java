package yoha.seckill.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import yoha.seckill.dao.SeckillDao;
import yoha.seckill.dao.SuccessKilledDao;
import yoha.seckill.dto.Exposer;
import yoha.seckill.dto.SeckillResult;
import yoha.seckill.entity.Seckill;
import yoha.seckill.entity.SuccessKilled;
import yoha.seckill.enums.SeckillStateEnum;
import yoha.seckill.exception.RepeatKillException;
import yoha.seckill.exception.SeckillCloseException;
import yoha.seckill.exception.SeckillException;
import yoha.seckill.service.SeckillService;

import java.util.Date;
import java.util.List;
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //MD5盐值字符串
    private String salt = "SJDOIWdwalk3#@E890D*)(";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        //查询失败
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        //查询成功，但未达到开始时间或已超出结束时间
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //查询成功，生成秒杀地址并暴露出去
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    public SeckillResult executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill url rewrite");
        }
        try {
            // 执行秒杀操作：减库存 + 记录秒杀行为
            int reduceNumber = seckillDao.reduceNumber(seckillId, new Date());
            if (reduceNumber <= 0) {
                //没有更新到减库存操作，统一归结为秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                //记录秒杀行为
                int insertNumber = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertNumber <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("seckill repeat");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillResult(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //将所有编译器异常转化为运行期异常，方便spring事务捕获异常正确rollback
            throw new SeckillException("seckill inner exception");
        }
    }
}
