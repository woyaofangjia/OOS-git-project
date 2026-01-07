package com.wsk.controller;

import com.wsk.pojo.UserInformation;
import com.wsk.pojo.UserPassword;
import com.wsk.response.BaseResponse;
import com.wsk.service.UserInformationService;
import com.wsk.service.UserPasswordService;
import com.wsk.tool.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 注册控制器，处理用户注册相关的请求
 * 负责用户注册逻辑处理和响应
 */
@Controller
public class RegisterController {
    /**
     * 用户密码服务
     */
    @Resource
    private UserPasswordService userPasswordService;

    /**
     * 用户信息服务
     */
    @Resource
    private UserInformationService userInformationService;

    /**
     * 用户注册方法
     * @param request HttpServletRequest对象
     * @param password 用户密码
     * @param token 防重复提交令牌
     * @param phone 手机号码（可选）
     * @return BaseResponse对象，包含注册结果信息
     */
    @RequestMapping("/insertUser.do")
    @ResponseBody
    public BaseResponse insertUser(HttpServletRequest request, @RequestParam String password, @RequestParam String token, @RequestParam(required = false) String phone) {
        //双重保障：优先使用请求参数中的phone，其次使用session中的phone
        String realPhone = phone;
        if (StringUtils.getInstance().isNullOrEmpty(realPhone)) {
            realPhone = (String) request.getSession().getAttribute("phone");
        }
        
        //验证手机号
        if (StringUtils.getInstance().isNullOrEmpty(realPhone) || !StringUtils.getInstance().isPhone(realPhone)) {
            return BaseResponse.fail("手机号格式不正确");
        }
        
        //token，唯一标识
        String insertUserToken = (String) request.getSession().getAttribute("token");
        //防止重复提交
        if (StringUtils.getInstance().isNullOrEmpty(insertUserToken) || !insertUserToken.equals(token)) {
            return BaseResponse.fail("Token验证失败，请重新尝试");
        }
        
        //将手机号保存到session
        request.getSession().setAttribute("phone", realPhone);
        
        //该手机号码已经存在
        int uid = userInformationService.selectIdByPhone(realPhone);
        if (uid != 0) {
            return BaseResponse.fail("该手机号已经被注册");
        }

        //用户信息
        UserInformation userInformation = new UserInformation();
        userInformation.setPhone(realPhone);
        userInformation.setCreatetime(new Date());
        String username = (String) request.getSession().getAttribute("name");
        //如果用户名不存在，使用手机号后4位作为用户名
        if (StringUtils.getInstance().isNullOrEmpty(username)) {
            username = "用户" + realPhone.substring(realPhone.length() - 4);
        }
        userInformation.setUsername(username);
        userInformation.setModified(new Date());
        int result;
        
        try {
            result = userInformationService.insertSelective(userInformation);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.fail("插入用户信息失败");
        }
        //如果用户基本信息写入成功
        if (result == 1) {
            uid = userInformationService.selectIdByPhone(realPhone);
            String newPassword = StringUtils.getInstance().getMD5(password);
            UserPassword userPassword = new UserPassword();
            userPassword.setModified(new Date());
            userPassword.setUid(uid);
            userPassword.setPassword(newPassword);
            result = userPasswordService.insertSelective(userPassword);
            //密码写入失败
            if (result != 1) {
                userInformationService.deleteByPrimaryKey(uid);
                return BaseResponse.fail("密码设置失败");
            } else {
                //注册成功
                userInformation = userInformationService.selectByPrimaryKey(uid);
                request.getSession().setAttribute("userInformation", userInformation);
                return BaseResponse.success();
            }
        }
        return BaseResponse.fail("注册失败，请稍后重试");
    }
}
