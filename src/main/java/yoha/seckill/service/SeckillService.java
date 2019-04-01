package yoha.seckill.service;

import yoha.seckill.dto.Exposer;
import yoha.seckill.dto.SeckillExecution;
import yoha.seckill.entity.Seckill;
import yoha.seckill.exception.RepeatKillException;
import yoha.seckill.exception.SeckillCloseException;
import yoha.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口
 * 方法粒度、参数、返回类型（类型/异常）
 */
public interface SeckillService {
    /**
     * 获取所有秒杀商品
     */
    List<Seckill> getSeckillList();

    /**
     * 获取单个秒杀商品
     */
    Seckill getSeckillById(long seckillId);

    /**
     * 秒杀开启时暴露秒杀接口地址，否则输出系统时间和秒杀开启时间
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId 秒杀商品ID
     * @param userPhone 用户信息
     * @param md5 秒杀接口验证
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 通过存储过程来执行秒杀操作
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
