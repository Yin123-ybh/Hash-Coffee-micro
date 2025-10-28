package com.coffee.productservice.controller;

import com.coffee.common.result.Result;
import com.coffee.productservice.entity.Product;
import com.coffee.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> getProductList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        try {
            log.info("接收商品列表请求: name={}, categoryId={}, status={}, page={}, size={}", 
                    name, categoryId, status, page, size);
            
            // 获取所有商品
            List<Product> allProducts = productService.getProductList(name, categoryId, status);
            
            // 构建分页结果
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("records", allProducts);
            result.put("total", allProducts.size());
            result.put("current", page);
            result.put("size", size);
            
            log.info("返回商品数量: {}", allProducts.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询商品列表失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getCategories() {
        try {
            log.info("接收商品分类请求");
            List<Map<String, Object>> categories = productService.getCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("查询分类失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return Result.notFound("商品不存在");
            }
            return Result.success(product);
        } catch (Exception e) {
            log.error("查询商品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/add")
    public Result<Product> addProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.addProduct(product);
            return Result.success("添加成功", savedProduct);
        } catch (Exception e) {
            log.error("添加商品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public Result<Boolean> updateProduct(@RequestBody Product product) {
        try {
            boolean success = productService.updateProduct(product);
            return success ? Result.success("更新成功", true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProduct(@PathVariable Long id) {
        try {
            boolean success = productService.deleteProduct(id);
            return success ? Result.success("删除成功", true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/recommended")
    public Result<List<Product>> getRecommendedProducts() {
        try {
            List<Product> products = productService.getRecommendedProducts();
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取推荐商品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts() {
        try {
            List<Product> products = productService.getHotProducts();
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取热销商品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/stock")
    public Result<Boolean> updateProductStock(@RequestParam Long productId, @RequestParam Integer stock) {
        try {
            boolean success = productService.updateProductStock(productId, stock);
            return success ? Result.success("库存更新成功", true) : Result.error("库存更新失败");
        } catch (Exception e) {
            log.error("更新商品库存失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
