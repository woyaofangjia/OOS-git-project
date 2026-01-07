package com.wsk.controller;

import com.wsk.pojo.AllKinds;
import com.wsk.pojo.Classification;
import com.wsk.pojo.Specific;
import com.wsk.service.AllKindsService;
import com.wsk.service.ClassificationService;
import com.wsk.service.SpecificeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Ajax控制器，处理前端异步请求
 */
@Controller
@RequestMapping("/ajax")
public class AjaxController {
    
    @Resource
    private AllKindsService allKindsService;
    
    @Resource
    private ClassificationService classificationService;
    
    @Resource
    private SpecificeService specificeService;
    
    /**
     * 获取所有一级分类
     * 对应前端/ajax/getAllKinds.do请求
     */
    @RequestMapping(value = "/getAllKinds.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<AllKinds> getAllKinds() {
        return allKindsService.selectAll();
    }
    
    /**
     * 根据一级分类ID获取二级分类（包含对应的三级分类数据）
     * 对应前端/ajax/getClassification.do请求
     */
    @RequestMapping(value = "/getClassification.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<Classification> getClassificationByPid(@RequestParam int pid) {
        // 现在classificationService.selectByPid方法会返回包含三级分类数据的Classification对象
        return classificationService.selectByPid(pid);
    }
    
    /**
     * 根据二级分类ID获取三级分类
     * 对应前端可能的/ajax/getSpecific.do请求
     */
    @RequestMapping(value = "/getSpecific.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<Specific> getSpecificByCid(@RequestParam int cid) {
        return specificeService.selectByCid(cid);
    }
}
