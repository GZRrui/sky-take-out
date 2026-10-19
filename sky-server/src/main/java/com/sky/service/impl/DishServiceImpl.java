package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
public class DishServiceImpl implements DishService {
    
    @Autowired 
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional 
    public void saveWithFlavor(DishDTO dishDto) {
       
        Dish dish = new Dish();
        

        BeanUtils.copyProperties(dishDto, dish);
        //向菜品表插入一条数据

        dishMapper.insert(dish);

        //获取INSERT语句生成主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null && flavors.size() > 0) {
           flavors.forEach(flavor -> {
               flavor.setDishId(dishId);
           });
            //向口味表插入多条数据
           dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 菜品批量删除
     * @return
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids){
        //判断当前菜品是否能够删除--是否在起售中
        for (Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除--是否套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中菜品数据
        for (Long id : ids ){
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
 
    }

     /**
     * 根据id来查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Override 
    public DishVO getByIdWithFlavor(Long id){
        //查菜品数据
        Dish dish = dishMapper.getById(id);
        
        //查口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        
        //上述数据封装到DishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        
        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和口味数据
     * @param dishDto
     * @return
     */
    @Override
    public void updateWithFlavor(DishDTO dishDto){
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
       
        //修改菜品表基本信息
        dishMapper.update(dish);

        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDto.getId());

        //重新插入口味数据
        List<DishFlavor> flavors =dishDto.getFlavors();
        if (flavors != null && flavors.size() > 0) {
           flavors.forEach(flavor -> {
               flavor.setDishId(dishDto.getId());
           });
            //向口味表插入多条数据
           dishFlavorMapper.insertBatch(flavors);
        }
    }
}