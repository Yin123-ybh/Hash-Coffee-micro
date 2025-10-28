package com.coffee.productservice.service;

import com.coffee.productservice.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 获取所有分类
     */
    List<Category> getAllCategories();
    
    /**
     * 根据ID获取分类
     */
    Category getCategoryById(Long id);
    
    /**
     * 创建分类
     */
    Category createCategory(Category category);
    
    /**
     * 更新分类
     */
    Category updateCategory(Category category);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long id);
}
