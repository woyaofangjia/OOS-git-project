package com.wsk.service.Impl;

import com.wsk.dao.UserInformationMapper;
import com.wsk.pojo.UserInformation;
import com.wsk.service.UserInformationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户信息服务实现类
 * 实现用户信息管理的具体业务逻辑
 */
@Service("userInformationService")
public class UserInformationServiceImpl implements UserInformationService {
    /**
     * 用户信息数据访问对象
     */
    @Resource
    private UserInformationMapper userInformationMapper;

    /**
     * 根据主键删除用户信息
     * @param id 用户ID
     * @return 影响行数
     */
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    /**
     * 插入完整用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    @Override
    public int insert(UserInformation record) {
        return this.userInformationMapper.insert(record);
    }

    /**
     * 选择性插入用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    @Override
    public int insertSelective(UserInformation record) {
        return this.userInformationMapper.insertSelective(record);
    }

    /**
     * 根据主键查询用户信息
     * @param id 用户ID
     * @return 用户信息对象
     */
    @Override
    public UserInformation selectByPrimaryKey(Integer id) {
        return this.userInformationMapper.selectByPrimaryKey(id);
    }

    /**
     * 选择性更新用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    @Override
    public int updateByPrimaryKeySelective(UserInformation record) {
        return this.userInformationMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 更新完整用户信息
     * @param record 用户信息对象
     * @return 影响行数
     */
    @Override
    public int updateByPrimaryKey(UserInformation record) {
        return this.userInformationMapper.updateByPrimaryKey(record);
    }

    /**
     * 根据手机号查询用户ID
     * @param phone 手机号
     * @return 用户ID，不存在返回0
     */
    @Override
    public int selectIdByPhone(String phone) {
        try {
            return this.userInformationMapper.selectIdByPhone(phone);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 根据ID列表批量查询用户信息
     * @param list 用户ID列表
     * @return 用户信息列表
     */
    @Override
    public List<UserInformation> getAllForeach(List<Integer> list) {
        return this.userInformationMapper.getAllForeach(list);
    }
}
