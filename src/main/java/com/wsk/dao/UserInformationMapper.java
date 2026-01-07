package com.wsk.dao;

import com.wsk.pojo.UserInformation;

import java.util.List;

/**
 * 用户信息数据访问接口
 * 提供用户信息相关的数据库操作方法
 */
public interface UserInformationMapper {
    /**
     * 根据主键删除用户信息
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入完整用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    int insert(UserInformation record);

    /**
     * 选择性插入用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    int insertSelective(UserInformation record);

    /**
     * 根据主键查询用户信息
     * @param id 用户ID
     * @return 用户信息对象
     */
    UserInformation selectByPrimaryKey(Integer id);

    /**
     * 选择性更新用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserInformation record);

    /**
     * 更新完整用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    int updateByPrimaryKey(UserInformation record);

    /**
     * 根据手机号查询用户ID
     * @param phone 手机号
     * @return 用户ID
     */
    int selectIdByPhone(String phone);

    /**
     * 根据ID列表批量查询用户信息
     * @param list 用户ID列表
     * @return 用户信息列表
     */
    List<UserInformation> getAllForeach(List<Integer> list);
}