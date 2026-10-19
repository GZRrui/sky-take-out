package com.sky.service;

import java.util.List;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

public interface DishService {
    
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    public void saveWithFlavor(DishDTO dishDto);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @return
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id来查询菜品和对应的口味数据
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id);

     /**
     * 根据id修改菜品基本信息和口味数据
     * @param dishDto
     * @return
     */
    void updateWithFlavor(DishDTO dishDto);
}