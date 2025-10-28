package com.coffee.productservice.mapper;

import com.coffee.productservice.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 获取所有分类
     */
    @Select("SELECT * FROM categories")
    List<Category> selectAll();
    
    /**
     * 根据ID获取分类
     */
    @Select("SELECT * FROM categories WHERE id = #{id}")
    Category selectById(Long id);
    
    /**
     * 插入分类
     */
    @Insert("INSERT INTO categories (name, description, image_url, status, create_time, update_time) " +
            "VALUES (#{name}, #{description}, #{imageUrl}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);
    
    /**
     * 更新分类
     */
    @Update("UPDATE categories SET name = #{name}, description = #{description}, image_url = #{imageUrl}, " +
            "status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    void update(Category category);
    
    /**
     * 根据ID删除分类
     */
    @Delete("DELETE FROM categories WHERE id = #{id}")
    void deleteById(Long id);
}









