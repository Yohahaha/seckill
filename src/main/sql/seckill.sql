-- 秒杀操作存储过程
delimiter $$ -- 将结束符由分号转换成$$

-- 定义存储过程
-- row_count()：获取上一条修改类型（update，delete，insert）的sql的影响行数
--     返回值： 0-未修改数据，>0-修改的行数，<0-错误
create procedure `seckill`.`execute_seckill`
  (in v_seckill_id bigint, in v_phone bigint, in v_kill_time timestamp, out r_result int)
  begin
    declare insert_count int default 0;
    start transaction ;
    insert ignore into success_killed (seckill_id, user_phone, create_time)
      values(v_seckill_id, v_phone, v_kill_time);
    select row_count() into insert_count;
    if (insert_count = 0) then
      rollback ;
      set r_result = -1;
    elseif (insert_count < 0) then
      rollback ;
      set r_result = -2;
    else
      update seckill set number = number - 1
      where seckill_id = v_seckill_id and v_kill_time < end_time and v_kill_time > start_time and number > 0;
      select row_count() into insert_count;
      IF (insert_count = 0) THEN
        ROLLBACK;
        set r_result = 0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK;
        set r_result = -2;
      ELSE
        COMMIT;
        set r_result = 1;
      END IF;
    END IF;
  END;
$$
-- 存储过程结束
-- 改回来结束符
delimiter ;
set @r_result = -3
-- 执行存储过程
call execute_seckill(1003,13344223355,now(),@r_result);
-- 获取结果
select @r_result;
