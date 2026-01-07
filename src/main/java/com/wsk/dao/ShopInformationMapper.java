package com.wsk.dao;

import com.wsk.pojo.ShopInformation;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ShopInformationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShopInformation record);

    int insertSelective(ShopInformation record);

    ShopInformation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShopInformation record);

    int updateByPrimaryKey(ShopInformation record);

    List<ShopInformation> selectTen(Map map);

    List<ShopInformation> selectOffShelf(Integer uid, Integer start);

    int getCountsOffShelf(Integer uid);

    int getCounts();

    Integer selectIdByImage(String image);

    List<ShopInformation> selectByName(String name);

    //通过分类选择
    List<ShopInformation> selectBySort(int sort);

    //选择用户的发布 - 显示用户所有已发布商品(无论审核状态)，方便用户查看自己的发布
    @Select("select * from shopinformation where uid=#{uid} and display=1 order by id desc limit 12")
    List<ShopInformation> selectUserReleaseByUid(int uid);
    
    //按审核状态查询商品
    List<ShopInformation> selectByAuditStatus(int auditStatus);
    
    //更新商品审核状态
    int updateAuditStatus(Integer id, Integer auditStatus);
}