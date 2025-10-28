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
    
    /**
     * 查询秒杀活动列表
     */
    List<SeckillActivity> selectSeckillList(
            @Param("name") String name,
            @Param("status") Integer status,
            @Param("couponId") Long couponId);
    
    /**
     * 统计秒杀活动总数
     */
    long countSeckills(
            @Param("name") String name,
            @Param("status") Integer status,
            @Param("couponId") Long couponId);
    
    /**
     * 插入秒杀活动
     */
    int insert(SeckillActivity activity);
}

