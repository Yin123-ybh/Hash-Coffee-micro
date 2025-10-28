package com.coffee.productservice.service.impl;

import com.coffee.productservice.entity.Category;
import com.coffee.productservice.mapper.CategoryMapper;
import com.coffee.productservice.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public List<Category> getAllCategories() {
        log.info("获取所有分类");
        return categoryMapper.selectAll();
    }
    
    @Override
    public Category getCategoryById(Long id) {
        log.info("根据ID获取分类: {}", id);
        return categoryMapper.selectById(id);
    }
    
    @Override
    public Category createCategory(Category category) {
        log.info("创建分类: {}", category.getName());
        categoryMapper.insert(category);
        return category;
    }
    
    @Override
    public Category updateCategory(Category category) {
        log.info("更新分类: {}", category.getId());
        categoryMapper.update(category);
        return category;
    }
    
    @Override
    public void deleteCategory(Long id) {
        log.info("删除分类: {}", id);
        categoryMapper.deleteById(id);
    }
}

