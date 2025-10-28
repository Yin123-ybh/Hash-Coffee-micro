package com.coffee.productservice.service;

import com.coffee.productservice.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 获取所有商品
     */
    List<Product> getAllProducts();
    
    /**
     * 根据ID获取商品
     */
    Product getProductById(Long id);
    
    /**
     * 根据分类获取商品
     */
    List<Product> getProductsByCategory(Long categoryId);
    
    /**
     * 创建商品
     */
    Product createProduct(Product product);
    
    /**
     * 添加商品
     */
    Product addProduct(Product product);
    
    /**
     * 更新商品
     */
    boolean updateProduct(Product product);
    
    /**
     * 删除商品
     */
    boolean deleteProduct(Long id);
    
    /**
     * 获取商品列表（带条件查询）
     */
    List<Product> getProductList(String name, Long categoryId, Integer status);
    
    /**
     * 获取推荐商品
     */
    List<Product> getRecommendedProducts();
    
    /**
     * 获取热销商品
     */
    List<Product> getHotProducts();
    
    /**
     * 更新商品库存
     */
    boolean updateProductStock(Long productId, Integer stock);
    
    /**
     * 获取商品分类
     */
    List<Map<String, Object>> getCategories();
}