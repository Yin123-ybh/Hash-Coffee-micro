package com.coffee.productservice.mapper;

import com.coffee.productservice.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 获取所有商品
     */
    @Select("SELECT * FROM products")
    List<Product> selectAll();
    
    /**
     * 根据ID获取商品
     */
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product selectById(Long id);
    
    /**
     * 根据分类ID获取商品
     */
    @Select("SELECT * FROM products WHERE category_id = #{categoryId}")
    List<Product> selectByCategoryId(Long categoryId);
    
    /**
     * 插入商品
     */
    @Insert("INSERT INTO products (name, description, price, image_url, category_id, stock, status, create_time, update_time) " +
            "VALUES (#{name}, #{description}, #{price}, #{imageUrl}, #{categoryId}, #{stock}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);
    
    /**
     * 更新商品
     */
    @Update("UPDATE products SET name = #{name}, description = #{description}, price = #{price}, " +
            "image_url = #{imageUrl}, category_id = #{categoryId}, stock = #{stock}, status = #{status}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    void update(Product product);
    
    /**
     * 根据ID删除商品
     */
    @Delete("DELETE FROM products WHERE id = #{id}")
    void deleteById(Long id);
    
    /**
     * 获取商品分类列表
     */
    List<Map<String, Object>> selectCategories();
    
    /**
     * 根据条件查询商品列表
     */
    List<Product> selectProductsByNameAndCategory(@Param("name") String name, 
                                                   @Param("categoryId") Long categoryId, 
                                                   @Param("status") Integer status);
}
