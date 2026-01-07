package com.wsk.service.Impl;

import com.wsk.dao.UserPasswordMapper;
import com.wsk.pojo.UserPassword;
import com.wsk.service.UserPasswordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户密码服务实现类
 * 实现用户密码管理的具体业务逻辑
 */
@Service("userPasswordService")
public class UserPasswordServiceImpl implements UserPasswordService{
    /**
     * 用户密码数据访问对象
     */
    @Resource
    private UserPasswordMapper userPasswordMapper;

    /**
     * 根据主键删除用户密码
     * @param id 密码ID
     * @return 影响行数
     */
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    /**
     * 插入完整用户密码信息
     * @param record 用户密码对象
     * @return 影响行数
     */
    @Override
    public int insert(UserPassword record) {
        return userPasswordMapper.insert(record);
    }

    /**
     * 选择性插入用户密码信息
     * @param record 用户密码对象
     * @return 影响行数
     */
    @Override
    public int insertSelective(UserPassword record) {
        return userPasswordMapper.insertSelective(record);
    }

    /**
     * 根据主键查询用户密码
     * @param id 密码ID
     * @return 用户密码对象
     */
    @Override
    public UserPassword selectByPrimaryKey(Integer id) {
        return userPasswordMapper.selectByPrimaryKey(id);
    }

    /**
     * 选择性更新用户密码
     * @param record 用户密码对象
     * @return 影响行数
     */
    @Override
    public int updateByPrimaryKeySelective(UserPassword record) {
        return userPasswordMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 更新完整用户密码信息
     * @param record 用户密码对象
     * @return 影响行数
     */
    @Override
    public int updateByPrimaryKey(UserPassword record) {
        return userPasswordMapper.updateByPrimaryKey(record);
    }

    /**
     * 根据用户ID查询用户密码
     * @param uid 用户ID
     * @return 用户密码对象，不存在返回null
     */
    @Override
    public UserPassword selectByUid(Integer uid) {
        try {
            return this.userPasswordMapper.selectByUid(uid);
        } catch (Exception e) {
            return null;
        }
    }
}
