package com.wsk.service;

import com.wsk.pojo.UserPassword;

/**
 * 用户密码服务接口
 * 提供用户密码管理相关的业务逻辑方法
 */
public interface UserPasswordService {
    /**
     * 根据主键删除用户密码
     * @param id 密码ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入完整用户密码信息
     * @param record 用户密码对象
     * @return 影响行数
     */
    int insert(UserPassword record);

    /**
     * 选择性插入用户密码信息
     * @param record 用户密码对象
     * @return 影响行数
     */
    int insertSelective(UserPassword record);

    /**
     * 根据主键查询用户密码
     * @param id 密码ID
     * @return 用户密码对象
     */
    UserPassword selectByPrimaryKey(Integer id);

    /**
     * 选择性更新用户密码
     * @param record 用户密码对象
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserPassword record);

    /**
     * 更新完整用户密码信息
     * @param record 用户密码对象
     * @return 影响行数
     */
    int updateByPrimaryKey(UserPassword record);

    /**
     * 根据用户ID查询用户密码
     * @param uid 用户ID
     * @return 用户密码对象
     */
    UserPassword selectByUid(Integer uid);
}
