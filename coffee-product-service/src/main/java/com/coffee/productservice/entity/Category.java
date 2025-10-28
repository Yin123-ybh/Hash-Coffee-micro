package com.coffee.productservice.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
public class Category {
    
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 分类图片
     */
    private String image;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

