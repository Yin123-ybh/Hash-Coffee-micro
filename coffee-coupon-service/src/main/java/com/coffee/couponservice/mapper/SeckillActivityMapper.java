package com.coffee.couponservice.mapper;

import com.coffee.couponservice.entity.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 秒杀活动Mapper接口
 */
@Mapper
public interface SeckillActivityMapper {
    // 根据ID查询活动
    SeckillActivity selectById(@Param("id") Long id);

    // 查询所有活动
    List<SeckillActivity> selectAll();

    // 分页条件查询
    List<SeckillActivity> selectSeckillList(@Param("name") String name, @Param("status") Integer status, @Param("couponId") Long couponId);

    // 统计满足条件的秒杀活动数量
    int countSeckills(@Param("name") String name, @Param("status") Integer status, @Param("couponId") Long couponId);

    // 新增
    int insert(SeckillActivity activity);

    // 更新
    int update(SeckillActivity activity);

    // 删除
    int deleteById(@Param("id") Long id);

    // 乐观锁/扣减库存（可根据业务再补充，例如）
    int updateStockForSeckill(@Param("id") Long id, @Param("stock") Integer stock);
}