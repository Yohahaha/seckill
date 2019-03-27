package yoha.seckill.dao;

import org.apache.ibatis.annotations.Param;
import yoha.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
    /**
     * 插入秒杀成功信息
     * @param seckillId
     * @param userPhone
     * @return 插入数据库成功或失败的标识
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据秒杀商品的id查询successKilled信息，并携带Seckill实体
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

}
