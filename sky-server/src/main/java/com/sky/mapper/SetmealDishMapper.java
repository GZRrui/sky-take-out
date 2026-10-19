package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper 
public interface SetmealDishMapper {
    
    /**
     * 根据菜品id查询对应的套餐id
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in(1,2,3,4)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}