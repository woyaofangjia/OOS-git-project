package com.wsk.controller;

import com.wsk.pojo.ShopInformation;
import com.wsk.pojo.UserInformation;
import com.wsk.service.ShopInformationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员相关功能控制器
 */
@Controller
public class AdminController {

    @Resource
    private ShopInformationService shopInformationService;

    /**
     * 跳转到管理员审核页面
     */
    @RequestMapping(value = "/admin_audit.do", method = RequestMethod.GET)
    public String adminAudit(HttpServletRequest request, Model model) {
        // 判断是否是管理员登录
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType)) {
            // 非管理员跳转到首页
            return "redirect:/home.do";
        }

        // 确保userInformation不为null，防止header模板渲染错误
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (userInformation == null) {
            userInformation = new UserInformation();
            // 设置管理员默认名称
            userInformation.setUsername("管理员");
        }
        model.addAttribute("userInformation", userInformation);

        try {
            // 获取待审核的商品列表
            List<ShopInformation> pendingList = shopInformationService.selectByAuditStatus(0);
            // 获取已通过审核的商品列表
            List<ShopInformation> approvedList = shopInformationService.selectByAuditStatus(1);
            // 获取已拒绝的商品列表
            List<ShopInformation> rejectedList = shopInformationService.selectByAuditStatus(2);

            model.addAttribute("pendingList", pendingList);
            model.addAttribute("approvedList", approvedList);
            model.addAttribute("rejectedList", rejectedList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "获取商品审核列表失败");
        }

        return "page/admin/admin_audit";
    }

    /**
     * 处理商品审核操作
     */
    @RequestMapping(value = "/audit_product.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> auditProduct(@RequestParam int id, @RequestParam int auditStatus, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 判断是否是管理员登录
        String userType = (String) request.getSession().getAttribute("userType");
        if (!"admin".equals(userType)) {
            result.put("success", false);
            result.put("message", "您没有权限进行审核操作");
            return result;
        }

        try {
            // 获取商品信息
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
            if (shopInformation == null) {
                result.put("success", false);
                result.put("message", "商品不存在");
                return result;
            }

            // 更新审核状态
            shopInformation.setAuditStatus(auditStatus);
            // 当审核通过时，设置商品为显示状态
            if (auditStatus == 1) {
                shopInformation.setDisplay(1);
            }
            int updateResult = shopInformationService.updateByPrimaryKeySelective(shopInformation);

            if (updateResult > 0) {
                result.put("success", true);
                result.put("message", auditStatus == 1 ? "商品审核通过" : "商品审核拒绝");
            } else {
                result.put("success", false);
                result.put("message", "审核操作失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误，请稍后重试");
        }

        return result;
    }
}