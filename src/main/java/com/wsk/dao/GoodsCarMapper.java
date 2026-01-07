package com.wsk.dao;

import com.wsk.pojo.GoodsCar;

import java.util.List;

public interface GoodsCarMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GoodsCar record);

    int insertSelective(GoodsCar record);

    GoodsCar selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GoodsCar record);

    int updateByPrimaryKey(GoodsCar record);

    List<GoodsCar> selectByUid(int uid);
    
    // 根据商品ID删除购物车中的商品
    int deleteBySid(Integer sid);
}