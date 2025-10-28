package com.coffee.couponservice.service.impl;

import com.coffee.couponservice.entity.Coupon;
import com.coffee.couponservice.mapper.CouponMapper;
import com.coffee.couponservice.service.CouponQueryService;
import com.coffee.couponservice.vo.PublicCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponQueryServiceImpl implements CouponQueryService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<PublicCouponVO> listAvailableCoupons() {
        List<Coupon> list = couponMapper.selectAvailableCoupons();
        List<PublicCouponVO> result = new ArrayList<>();
        for (Coupon c : list) {
            PublicCouponVO vo = new PublicCouponVO();
            vo.setId(c.getId());
            vo.setTitle(c.getName());
            vo.setName(c.getName());
            vo.setDiscountAmount(BigDecimal.valueOf(c.getDiscountValue()));
            vo.setAmount(BigDecimal.valueOf(c.getDiscountValue()));
            vo.setThreshold(BigDecimal.valueOf(c.getMinAmount()));
            vo.setMinAmount(BigDecimal.valueOf(c.getMinAmount()));
            vo.setType(c.getType());
            vo.setStartTime(c.getStartTime());
            vo.setExpireTime(c.getEndTime());
            vo.setEndTime(c.getEndTime());
            
            // 暂时使用数据库库存，避免Redis连接问题
            Integer total = c.getTotalCount() == null ? 0 : c.getTotalCount();
            Integer used = c.getUsedCount() == null ? 0 : c.getUsedCount();
            int left = Math.max(0, total - used);
            
            vo.setTotal(total);
            vo.setUsed(used);
            
            vo.setStock(left);
            vo.setRemain(left);
            vo.setRemainCount(left);
            vo.setLeft(left);
            vo.setLeftCount(left);
            vo.setStatus(c.getStatus());
            result.add(vo);
        }
        return result;
    }
}
