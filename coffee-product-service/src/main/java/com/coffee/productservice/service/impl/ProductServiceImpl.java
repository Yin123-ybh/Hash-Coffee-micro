package com.coffee.productservice.service.impl;

import com.coffee.productservice.entity.Product;
import com.coffee.productservice.mapper.ProductMapper;
import com.coffee.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<Product> getAllProducts() {
        log.info("获取所有商品");
        return productMapper.selectAll();
    }
    
    @Override
    public Product getProductById(Long id) {
        log.info("根据ID获取商品: {}", id);
        return productMapper.selectById(id);
    }
    
    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        log.info("根据分类获取商品: {}", categoryId);
        return productMapper.selectByCategoryId(categoryId);
    }
    
    @Override
    public Product createProduct(Product product) {
        log.info("创建商品: {}", product.getName());
        productMapper.insert(product);
        return product;
    }
    
    @Override
    public Product addProduct(Product product) {
        log.info("添加商品: {}", product.getName());
        productMapper.insert(product);
        return product;
    }
    
    @Override
    public boolean updateProduct(Product product) {
        log.info("更新商品: {}", product.getId());
        try {
            productMapper.update(product);
            return true;
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deleteProduct(Long id) {
        log.info("删除商品: {}", id);
        try {
            productMapper.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Product> getProductList(String name, Long categoryId, Integer status) {
        log.info("获取商品列表: name={}, categoryId={}, status={}", name, categoryId, status);
        
        // 如果指定了分类ID，使用分类查询
        if (categoryId != null && categoryId != 0) {
            log.info("按分类查询: categoryId={}", categoryId);
            return productMapper.selectByCategoryId(categoryId);
        }
        
        // 否则查询所有商品（简化实现）
        return productMapper.selectAll();
    }
    
    @Override
    public List<Product> getRecommendedProducts() {
        log.info("获取推荐商品");
        // 简化实现，返回前10个商品
        return productMapper.selectAll();
    }
    
    @Override
    public List<Product> getHotProducts() {
        log.info("获取热销商品");
        // 简化实现，返回前10个商品
        return productMapper.selectAll();
    }
    
    @Override
    public boolean updateProductStock(Long productId, Integer stock) {
        log.info("更新商品库存: productId={}, stock={}", productId, stock);
        try {
            Product product = productMapper.selectById(productId);
            if (product != null) {
                product.setStock(stock);
                productMapper.update(product);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新商品库存失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getCategories() {
        log.info("获取商品分类");
        try {
            // 从数据库查询分类
            List<Map<String, Object>> categories = productMapper.selectCategories();
            return categories;
        } catch (Exception e) {
            log.error("获取分类失败: {}", e.getMessage(), e);
            // 返回空列表而不是抛出异常
            return new ArrayList<>();
        }
    }
}
