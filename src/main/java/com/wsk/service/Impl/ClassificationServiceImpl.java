package com.wsk.service.Impl;

import com.wsk.dao.ClassificationMapper;
import com.wsk.dao.SpecificMapper;
import com.wsk.pojo.Classification;
import com.wsk.pojo.Specific;
import com.wsk.service.ClassificationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by wsk1103 on 2017/5/13.
 */
@Service
public class ClassificationServiceImpl implements ClassificationService {

    @Resource
    private ClassificationMapper classificationMapper;
    
    @Resource
    private SpecificMapper specificMapper;
    
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(Classification record) {
        return classificationMapper.insert(record);
    }

    @Override
    public int insertSelective(Classification record) {
        return classificationMapper.insertSelective(record);
    }

    @Override
    public Classification selectByPrimaryKey(Integer id) {
        return classificationMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Classification record) {
        return classificationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Classification record) {
        return classificationMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Classification> selectByPid(int pid) {
        try {
            // System.out.println("开始查询二级分类，pid: " + pid);
            // 获取二级分类列表
            List<Classification> classifications = classificationMapper.selectByPid(pid);
            // System.out.println("查询到的二级分类数量: " + (classifications != null ? classifications.size() : 0));
            
            // 为每个二级分类加载对应的三级分类数据
            if (classifications != null && !classifications.isEmpty()) {
                for (Classification classification : classifications) {
                    try {
                        // 获取当前二级分类ID
                        int cid = classification.getId();
                        // System.out.println("为二级分类ID: " + cid + " 加载三级分类");
                        // 加载对应的三级分类数据
                        List<Specific> specifics = specificMapper.selectByCid(cid);
                        // System.out.println("为二级分类ID: " + cid + " 查询到的三级分类数量: " + (specifics != null ? specifics.size() : 0));
                        // 将三级分类数据添加到二级分类对象的content属性中，供前端使用
                        classification.setContent(specifics);
                    } catch (Exception e) {
                        // System.out.println("加载三级分类时出错，分类ID: " + classification.getId());
                        // e.printStackTrace();
                        // 出错时设置空列表，避免影响其他分类
                        classification.setContent(new ArrayList<>());
                    }
                }
            }
            
            return classifications;
        } catch (Exception e) {
            // System.out.println("查询二级分类时发生异常，pid: " + pid);
            // e.printStackTrace();
            // 发生异常时返回空列表，避免前端出错
            return new ArrayList<>();
        }
    }
}
