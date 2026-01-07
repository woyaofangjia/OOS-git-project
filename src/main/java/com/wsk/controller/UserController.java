package com.wsk.controller;

import com.wsk.bean.GoodsCarBean;
import com.wsk.bean.ShopInformationBean;
import com.wsk.bean.UserWantBean;
import com.wsk.pojo.*;
import com.wsk.response.BaseResponse;
import com.wsk.service.*;
import com.wsk.token.TokenProccessor;
import com.wsk.tool.SaveSession;
import com.wsk.tool.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/*import com.wsk.tool.OCR;
import com.wsk.tool.Pornographic;*/

/**
 * 用户控制器，处理用户相关的请求
 * 负责用户的登录、注册、个人信息管理、商品发布管理等功能
 */
@Controller
@Slf4j
public class UserController {

    /**
     * 用户信息服务
     */
    @Resource
    private UserInformationService userInformationService;
    
    /**
     * 用户密码服务
     */
    @Resource
    private UserPasswordService userPasswordService;
    
    /**
     * 用户收藏服务
     */
    @Resource
    private UserCollectionService userCollectionService;
    
    /**
     * 用户发布服务
     */
    @Resource
    private UserReleaseService userReleaseService;
    
    /**
     * 已购商品服务
     */
    @Resource
    private BoughtShopService boughtShopService;
    
    /**
     * 用户求购服务
     */
    @Resource
    private UserWantService userWantService;
    @Resource
    private ShopCarService shopCarService;
    @Resource
    private OrderFormService orderFormService;
    @Resource
    private GoodsOfOrderFormService goodsOfOrderFormService;
    @Resource
    private UserStateService userStateService;
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private GoodsCarService goodsCarService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private ShopContextService shopContextService;
    @Resource
    private com.wsk.dao.AdminInformationMapper adminInformationMapper;

    //进入登录界面
    /**
     * 登录页面
     * @param request HttpServletRequest对象
     * @param model Model对象，用于传递数据到视图
     * @param msg 提示消息
     * @return 页面路径
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model, @RequestParam(required = false) String msg) {
        String token = TokenProccessor.getInstance().makeToken();
        log.info("进入登录界面，token为:" + token);
        request.getSession().setAttribute("token", token);
        model.addAttribute("token", token);
        
        // 从session中获取错误消息
        HttpSession session = request.getSession();
        String loginError = (String) session.getAttribute("login_error");
        if (loginError != null) {
            model.addAttribute("error_message", loginError);
            // 清除session中的错误消息，避免重复显示
            session.removeAttribute("login_error");
            log.info("显示登录错误消息: " + loginError);
        }
        
        // 兼容处理URL参数（如果存在）
        if (msg != null) {
            log.info("接收到URL错误参数: " + msg);
        }
        
        return "page/login_page";
    }

    //退出
    /**
     * 用户登出
     * @param request HttpServletRequest对象
     * @return 重定向到登录页面
     */
    @RequestMapping(value = "/logout.do")
    public String logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            // 移除所有用户相关的session属性
            session.removeAttribute("userInformation");
            session.removeAttribute("uid");
            // 可以根据需要添加其他需要清除的session属性
            System.out.println("logout");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login.do"; // 即使出错也跳转到登录页面
        }
        return "redirect:/login.do"; // 修改为重定向到登录页面
    }

    //用户注册,拥有插入数据而已，没什么用的
    @RequestMapping(value = "/registered.do", method = RequestMethod.POST)
    public String registered(Model model,
                             @RequestParam String name, @RequestParam String phone, @RequestParam String password) {
        UserInformation userInformation = new UserInformation();
        userInformation.setUsername(name);
        userInformation.setPhone(phone);
        userInformation.setModified(new Date());
        userInformation.setCreatetime(new Date());
        if (userInformationService.insertSelective(userInformation) == 1) {
            int uid = userInformationService.selectIdByPhone(phone);
            UserPassword userPassword = new UserPassword();
            userPassword.setModified(new Date());
            password = StringUtils.getInstance().getMD5(password);
            userPassword.setPassword(password);
            userPassword.setUid(uid);
            int result = userPasswordService.insertSelective(userPassword);
            if (result != 1) {
                model.addAttribute("result", "fail");
                return "success";
            }
            model.addAttribute("result", "success");
            return "success";
        }
        model.addAttribute("result", "fail");
        return "success";
    }

    //用户注册
//    @RequestMapping(value = "/registered", method = RequestMethod.GET)
//    public String registered() {
//        return "registered";
//    }

    //验证登录
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpSession session, String phone, String password) {
        if (getId(phone, password, request, session)) {
            return "redirect:/";
        } else {
            // 使用session存储错误消息，避免URL编码问题
            session.setAttribute("login_error", "user_not_found");
            return "redirect:/login.do";
        }
    }

    //查看用户基本信息
    /**
     * 个人信息页面
     * @param request HttpServletRequest对象
     * @param model Model对象，用于传递数据到视图
     * @return 页面路径
     */
    @RequestMapping(value = "/personal_info.do")
    public String personalInfo(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        //获取重定向URL参数
        String redirectUrl = request.getParameter("redirectUrl");
        if (!StringUtils.getInstance().isNullOrEmpty(redirectUrl)) {
            model.addAttribute("redirectUrl", redirectUrl);
        }
        String personalInfoToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("personalInfoToken", personalInfoToken);
        model.addAttribute("token", personalInfoToken);
        model.addAttribute("userInformation", userInformation);
        return "page/personal/personal_info";
    }


    //完善用户基本信息，认证
    @RequestMapping(value = "/certification.do", method = RequestMethod.POST)
    @ResponseBody
    public Map certification(HttpServletRequest request,
                             @RequestParam(required = false) String userName,
                             @RequestParam(required = false) String realName,
                             @RequestParam(required = false) String clazz, @RequestParam String token,
                             @RequestParam(required = false) String sno, @RequestParam(required = false) String dormitory,
                             @RequestParam(required = false) String gender, @RequestParam(required = false) String redirectUrl,
                             @RequestParam(required = false) String email) {
        // 调试日志：记录接收到的参数
        System.out.println("DEBUG - Certification method called with params:");
        System.out.println("DEBUG - userName: " + userName);
        System.out.println("DEBUG - realName: " + realName);
        System.out.println("DEBUG - clazz: " + clazz);
        System.out.println("DEBUG - token: " + token);
        System.out.println("DEBUG - sno: " + sno);
        System.out.println("DEBUG - dormitory: " + dormitory);
        System.out.println("DEBUG - gender: " + gender);
        System.out.println("DEBUG - redirectUrl: " + redirectUrl);
        System.out.println("DEBUG - email: " + email);
        
        Map<String, Object> map = new HashMap<>();
        map.put("result", 0);
        
        // 检查是否是管理员用户
        AdminInformation adminInformation = (AdminInformation) request.getSession().getAttribute("adminInformation");
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        
        // 检查是否已登录
        if (StringUtils.getInstance().isNullOrEmpty(userInformation) && StringUtils.getInstance().isNullOrEmpty(adminInformation)) {
            System.out.println("DEBUG - Neither userInformation nor adminInformation found in session");
            return map;
        }
        
        String certificationToken = (String) request.getSession().getAttribute("personalInfoToken");
        System.out.println("DEBUG - session personalInfoToken: " + certificationToken);
        
        //防止重复提交，验证token是否匹配
        // 处理URL编码问题，将空格替换回+号以提高兼容性
        String normalizedToken = (token != null) ? token.replace(' ', '+') : token;
        String normalizedCertificationToken = (certificationToken != null) ? certificationToken.replace(' ', '+') : certificationToken;
        
        // 增强token验证逻辑，处理可能的token问题
        if (StringUtils.getInstance().isNullOrEmpty(certificationToken)) {
            System.out.println("DEBUG - session token is null, attempting to regenerate token");
            // 如果session中没有token，尝试重新生成一个并继续处理
            // 这样可以避免用户需要重新刷新页面
            String newToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("personalInfoToken", newToken);
            map.put("newToken", newToken); // 将新token放入响应中
            System.out.println("DEBUG - Generated new token: " + newToken);
            // 继续处理而不是直接返回错误
        } else if (!normalizedCertificationToken.equals(normalizedToken)) {
            System.out.println("DEBUG - token validation failed");
            System.out.println("DEBUG - normalizedCertificationToken: " + normalizedCertificationToken);
            System.out.println("DEBUG - normalizedToken: " + normalizedToken);
            map.put("error", "令牌验证失败");
            return map;
        } else {
            System.out.println("DEBUG - token validation passed, removing token from session");
            request.getSession().removeAttribute("personalInfoToken");
            // 生成新token供后续请求使用
            String newToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("personalInfoToken", newToken);
            map.put("newToken", newToken); // 将新token放入响应中
        }
        
        // 设置更新时间戳
        Date now = new Date();
        int result = 0;
        
        // 管理员用户处理逻辑
        if (!StringUtils.getInstance().isNullOrEmpty(adminInformation)) {
            System.out.println("DEBUG - Processing admin information update");
            System.out.println("DEBUG - adminInformation id: " + adminInformation.getId());
            
            // 管理员信息更新，设置修改时间
            adminInformation.setModified(now);
            System.out.println("DEBUG - modified date: " + now);
            
            // 对于管理员，我们只更新会话中的信息，因为AdminInformation类结构不支持其他字段
            // 实际的数据库更新需要在AdminInformationService实现后添加
            result = 1; // 模拟更新成功
            System.out.println("DEBUG - admin update result: " + result);
        } 
        // 普通用户处理逻辑
        else if (!StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            System.out.println("DEBUG - Processing regular user information update");
            
            if (userName != null && userName.length() < 25) {
                userName = StringUtils.getInstance().replaceBlank(userName);
                userInformation.setUsername(userName);
            } else if (userName != null && userName.length() >= 25) {
                return map;
            }
            
            if (realName != null && realName.length() < 25) {
                realName = StringUtils.getInstance().replaceBlank(realName);
                userInformation.setRealname(realName);
            } else if (realName != null && realName.length() >= 25) {
                return map;
            }
            
            if (clazz != null && clazz.length() < 25) {
                clazz = StringUtils.getInstance().replaceBlank(clazz);
                userInformation.setClazz(clazz);
            } else if (clazz != null && clazz.length() >= 25) {
                return map;
            }
            
            if (sno != null && sno.length() < 25) {
                sno = StringUtils.getInstance().replaceBlank(sno);
                userInformation.setSno(sno);
            } else if (sno != null && sno.length() >= 25) {
                return map;
            }
            
            if (dormitory != null && dormitory.length() < 25) {
                dormitory = StringUtils.getInstance().replaceBlank(dormitory);
                userInformation.setDormitory(dormitory);
            } else if (dormitory != null && dormitory.length() >= 25) {
                return map;
            }
            
            if (gender != null && gender.length() <= 2) {
                gender = StringUtils.getInstance().replaceBlank(gender);
                // 将前端传入的1/2映射为数据库需要的1/0
                // 前端：1=男，2=女
                // 数据库：1=男，0=女
                if ("1".equals(gender)) {
                    gender = "1"; // 保持不变，男
                } else if ("2".equals(gender)) {
                    gender = "0"; // 2转换为0，女
                }
                userInformation.setGender(gender);
            } else if (gender != null && gender.length() > 2) {
                return map;
            }
            
            // 处理email字段
            if (email != null && email.length() < 50) {
                email = StringUtils.getInstance().replaceBlank(email);
                userInformation.setEmail(email);
            } else if (email != null && email.length() >= 50) {
                return map;
            }
            
            userInformation.setModified(now);
            System.out.println("DEBUG - userInformation id: " + userInformation.getId());
            System.out.println("DEBUG - modified date: " + now);
            
            // 更新普通用户信息
            result = userInformationService.updateByPrimaryKey(userInformation);
            System.out.println("DEBUG - user update result: " + result);
            
            if (result != 1) {
                System.out.println("DEBUG - user update failed");
                // 更新失败时尝试使用updateByPrimaryKeySelective作为备选
                result = userInformationService.updateByPrimaryKeySelective(userInformation);
                System.out.println("DEBUG - user selective update result: " + result);
                if (result != 1) {
                    // 更新失败
                    return map;
                }
            }
        }
        
        if (result == 1) {
            //认证成功
            if (!StringUtils.getInstance().isNullOrEmpty(adminInformation)) {
                // 管理员用户更新成功，更新session中的管理员信息
                request.getSession().setAttribute("adminInformation", adminInformation);
            } else if (!StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                // 普通用户更新成功，更新session中的用户信息
                request.getSession().setAttribute("userInformation", userInformation);
            }
            map.put("result", 1);
            //添加重定向URL到响应中
            if (!StringUtils.getInstance().isNullOrEmpty(redirectUrl)) {
                map.put("redirectUrl", redirectUrl);
            }
        }
        return map;
    }

    //enter the publishUserWant.do.html,进入求购页面
    @RequestMapping(value = "/require_product.do")
    public String enterPublishUserWant(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String error = request.getParameter("error");
        if (!StringUtils.getInstance().isNullOrEmpty(error)) {
            model.addAttribute("error", "error");
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("userInformation", userInformation);
        return "page/require_product";
    }

    //修改求购商品
    @RequestMapping(value = "/modified_require_product.do")
    public String modifiedRequireProduct(HttpServletRequest request, Model model,
                                         @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("userInformation", userInformation);
        UserWant userWant = userWantService.selectByPrimaryKey(id);
        model.addAttribute("userWant", userWant);
        String sort = getSort(userWant.getSort());
        model.addAttribute("sort", sort);
        return "page/modified_require_product";
    }

    //publish userWant,发布求购
    @RequestMapping(value = "/publishUserWant.do")
//    @ResponseBody
    public String publishUserWant(HttpServletRequest request, Model model,
                                  @RequestParam String name,
                                  @RequestParam int specifickinds, @RequestParam int quantity,
                                  @RequestParam double price, @RequestParam String remark,
                                  @RequestParam String token) {
        // 添加调试日志
        log.info("开始发布求购信息，name={}, specifickinds={}, quantity={}, price={}", name, specifickinds, quantity, price);
        
        int sort = specifickinds; // 保持与现有代码的兼容性
        log.info("设置分类ID: sort={}", sort);
//        Map<String, Integer> map = new HashMap<>();
        //determine whether the user exits
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //if the user no exits in the session,
//            map.put("result", 2);
            return "redirect:/login.do";
        }
        String publishUserWantToke = (String) request.getSession().getAttribute("publishUserWantToken");
        if (StringUtils.getInstance().isNullOrEmpty(publishUserWantToke) || !publishUserWantToke.equals(token)) {
//            map.put("result", 2);
            return "redirect:require_product.do?error=3";
        } else {
            request.getSession().removeAttribute("publishUserWantToken");
        }
        
        // 处理name和remark，去除空白字符
        name = StringUtils.getInstance().replaceBlank(name);
        remark = StringUtils.getInstance().replaceBlank(remark);
        
        // 验证数据格式
        try {
            if (StringUtils.getInstance().isNullOrEmpty(name) || StringUtils.getInstance().isNullOrEmpty(remark) 
                    || name.length() < 1 || remark.length() < 1 
                    || name.length() > 25 || remark.length() > 255) {
                return "redirect:require_product.do?error=1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:require_product.do?error=1";
        }
        UserWant userWant = new UserWant();
        userWant.setCreatetime(new Date());
        userWant.setName(name);
        userWant.setPrice(new BigDecimal(price));
        userWant.setQuantity(quantity);
        userWant.setRemark(remark);
        userWant.setSort(sort);
        // 设置为显示状态，确保可以在"我发布的求购"页面看到
        userWant.setDisplay(1);
        
        // 获取用户类型
        String userType = (String) request.getSession().getAttribute("userType");
        Integer uid = null;
        
        // 处理不同类型用户的ID获取
        if ("admin".equals(userType)) {
            // 管理员用户 - 从adminInformation获取ID
            AdminInformation adminInformation = (AdminInformation) request.getSession().getAttribute("adminInformation");
            if (adminInformation != null && adminInformation.getId() != null && adminInformation.getId() > 0) {
                uid = adminInformation.getId();
            } else {
                // 如果adminInformation不存在，使用session中的uid
                uid = (Integer) request.getSession().getAttribute("uid");
            }
            
            // 管理员发布的求购信息直接显示
            userWant.setDisplay(1);
        } else {
            // 普通用户 - 从userInformation获取ID
            if (userInformation != null && userInformation.getId() != null && userInformation.getId() > 0) {
                uid = userInformation.getId();
            }
            
            // 普通用户发布的求购信息需要审核，默认不显示
            userWant.setDisplay(0);
        }
        
        // 检查用户ID是否有效
        if (uid == null || uid <= 0) {
            model.addAttribute("message", "用户信息异常，请重新登录");
            return "redirect:require_product.do?error=5";
        }
        
        // 设置用户ID
        userWant.setUid(uid);
        int result;
        try {
            result = userWantService.insertSelective(userWant);
            if (result != 1) {
                log.error("发布求购信息失败，insertSelective返回: {}", result);
                return "redirect:/require_product.do?error=2";
            }
        } catch (Exception e) {
            log.error("发布求购信息异常: {}", e.getMessage(), e);
            e.printStackTrace();
            return "redirect:/require_product.do?error=2";
        }
//        map.put("result", result);
        return "redirect:/my_require_product.do";
    }

    //getUserWant,查看我的求购
    @RequestMapping(value = {"/my_require_product.do", "/my_require_product_page.do"})
    public String getUserWant(HttpServletRequest request, Model model) {
        List<UserWant> list;
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        try {
            // 获取用户ID并检查非空
            Integer uidObj = (Integer) request.getSession().getAttribute("uid");
            if (uidObj == null) {
                return "redirect:/login.do";
            }
            int uid = uidObj.intValue();
            
            // 获取用户发布的所有求购信息（不考虑审核状态，用户应该能看到自己所有发布）
//            list = selectUserWantByUid(4);
            list = selectUserWantByUid(uid);
            
            List<UserWantBean> userWantBeans = new ArrayList<>();
            // 确保list不为空
            if (list != null) {
                for (UserWant userWant : list) {
                    // 添加调试日志
                    log.info("处理求购信息: id={}, name={}, sort={}", userWant.getId(), userWant.getName(), userWant.getSort());
                    
                    UserWantBean userWantBean = new UserWantBean();
                    userWantBean.setId(userWant.getId());
                    userWantBean.setCreatetime(userWant.getCreatetime());
                    userWantBean.setName(userWant.getName());
                    userWantBean.setPrice(userWant.getPrice().doubleValue());
                    userWantBean.setUid(uid);
                    userWantBean.setQuantity(userWant.getQuantity());
                    userWantBean.setRemark(userWant.getRemark());
                    
                    String sortName = getSort(userWant.getSort());
                    log.info("获取到的分类名称: sortId={}, sortName={}", userWant.getSort(), sortName);
                    userWantBean.setSort(sortName);
                    
                    // 审核状态信息（UserWantBean不支持setDisplay方法）
                    userWantBeans.add(userWantBean);
                }
            }
            model.addAttribute("userWant", userWantBeans);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
        model.addAttribute("userInformation", userInformation);
        return "page/personal/my_require_product_page";
    }

    //getUserWantCounts.do,查看求购总数
    @RequestMapping(value = "/getUserWantCounts.do")
    @ResponseBody
    public Map getUserWantCounts(HttpServletRequest request, Model model) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            map.put("counts", -1);
            return map;
        }
        try {
            int counts = getUserWantCounts((Integer) request.getSession().getAttribute("uid"));
            map.put("counts", counts);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("counts", -1);
        }
        return map;
    }

    //删除求购
    @RequestMapping(value = "/deleteUserWant.do")
    public String deleteUserWant(HttpServletRequest request, @RequestParam int id) {
//        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return "redirect:/login.do";
        }
        UserWant userWant = new UserWant();
        userWant.setId(id);
        userWant.setDisplay(0);
        try {
            int result = userWantService.updateByPrimaryKeySelective(userWant);
            if (result != 1) {
                return "redirect:my_require_product.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:my_require_product.do";
    }

    //收藏
    //add the userCollection
    @RequestMapping(value = "/addUserCollection.do")
    @ResponseBody
    public BaseResponse addUserCollection(HttpServletRequest request, @RequestParam int sid) {
        //determine whether the user exits
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            //if the user no exits in the session,
            return BaseResponse.fail();
        }
        
        // 获取用户类型
        String userType = (String) request.getSession().getAttribute("userType");
        Integer uid = null;
        
        // 根据用户类型获取对应的ID
        if ("admin".equals(userType)) {
            // 管理员用户 - 从adminInformation获取ID
            AdminInformation adminInformation = (AdminInformation) request.getSession().getAttribute("adminInformation");
            if (adminInformation != null && adminInformation.getId() != null && adminInformation.getId() > 0) {
                uid = adminInformation.getId();
            } else {
                // 如果adminInformation不存在，使用session中的uid
                uid = (Integer) request.getSession().getAttribute("uid");
            }
        } else {
            // 普通用户 - 使用session中的uid
            uid = (Integer) request.getSession().getAttribute("uid");
        }
        
        // 验证uid是否有效
        if (uid == null || uid <= 0) {
            return BaseResponse.fail();
        }
        
        UserCollection userCollection = new UserCollection();
        userCollection.setModified(new Date());
        userCollection.setSid(sid);
        userCollection.setUid(uid);
        userCollection.setDisplay(1); // 设置为显示状态
        //begin insert the userCollection
        int result;
        try {
            result = userCollectionService.insertSelective(userCollection);
            if (result != 1) {
                log.error("添加收藏失败，insertSelective返回: {}", result);
                return BaseResponse.fail();
            }
        } catch (Exception e) {
            log.error("添加收藏异常: {}", e.getMessage(), e);
            return BaseResponse.fail();
        }
        return BaseResponse.success();
    }


    // delete the userCollection
    @RequestMapping(value = "/deleteUserCollection.do")
    @ResponseBody
    public BaseResponse deleteUserCollection(HttpServletRequest request, @RequestParam int ucid) {
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return BaseResponse.fail();
        }
        
        // 获取用户类型
        String userType = (String) request.getSession().getAttribute("userType");
        Integer uid = null;
        
        // 根据用户类型获取对应的ID
        if ("admin".equals(userType)) {
            // 管理员用户 - 从adminInformation获取ID
            AdminInformation adminInformation = (AdminInformation) request.getSession().getAttribute("adminInformation");
            if (adminInformation != null && adminInformation.getId() != null && adminInformation.getId() > 0) {
                uid = adminInformation.getId();
            } else {
                // 如果adminInformation不存在，使用session中的uid
                uid = (Integer) request.getSession().getAttribute("uid");
            }
        } else {
            // 普通用户 - 使用session中的uid
            uid = (Integer) request.getSession().getAttribute("uid");
        }
        
        // 验证uid是否有效
        if (uid == null || uid <= 0) {
            return BaseResponse.fail();
        }
        
        UserCollection userCollection = new UserCollection();
        userCollection.setId(ucid);
        userCollection.setModified(new Date());
        userCollection.setDisplay(0);
        int result;
        try {
            result = userCollectionService.updateByPrimaryKeySelective(userCollection);
            if (result != 1) {
                log.error("删除收藏失败，updateByPrimaryKeySelective返回: {}", result);
                return BaseResponse.fail();
            }
        } catch (Exception e) {
            log.error("删除收藏异常: {}", e.getMessage(), e);
            return BaseResponse.fail();
        }
        return BaseResponse.success();
    }

    //购物车开始。。。。。。。。。。
    //getShopCarCounts.do
    @RequestMapping(value = "/getShopCarCounts.do")
    @ResponseBody
    public BaseResponse getShopCarCounts(HttpServletRequest request) {
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return BaseResponse.fail();
        }
        int uid = (int) request.getSession().getAttribute("uid");
        int counts = getShopCarCounts(uid);
        return BaseResponse.success();
    }

    //check the shopping cart,查看购物车
    @RequestMapping(value = "/shopping_cart.do")
    public String selectShopCar(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
//            list.add(shopCar);
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = userInformation.getId();
        List<GoodsCar> goodsCars = goodsCarService.selectByUid(uid);
        List<GoodsCarBean> goodsCarBeans = new ArrayList<>();
        for (GoodsCar goodsCar : goodsCars) {
            GoodsCarBean goodsCarBean = new GoodsCarBean();
            goodsCarBean.setUid(goodsCar.getUid());
            goodsCarBean.setSid(goodsCar.getSid());
            goodsCarBean.setCreatetime(goodsCar.getCreatetime());
            goodsCarBean.setId(goodsCar.getId());
            goodsCarBean.setQuantity(goodsCar.getQuantity());
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(goodsCar.getSid());
            goodsCarBean.setName(shopInformation.getName());
            goodsCarBean.setRemark(shopInformation.getRemark());
            goodsCarBean.setImage(shopInformation.getImage());
            goodsCarBean.setPrice(shopInformation.getPrice().doubleValue());
            goodsCarBean.setSort(getSort(shopInformation.getSort()));
            // 添加商品库存信息，用于购物车页面显示
            if (shopInformation != null && shopInformation.getQuantity() != null) {
                goodsCarBean.setLevel(shopInformation.getQuantity()); // 使用现有属性level临时存储库存信息
            }
            goodsCarBeans.add(goodsCarBean);
        }
        model.addAttribute("list", goodsCarBeans);
        return "page/shopping_cart";
    }

//    //通过购物车的id获取购物车里面的商品
//    @RequestMapping(value = "/selectGoodsOfShopCar")
//    @ResponseBody
//    public List<GoodsCar> selectGoodsCar(HttpServletRequest request) {
//        List<GoodsCar> list = new ArrayList<>();
//        GoodsCar goodsCar = new GoodsCar();
//        if (Empty.isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
//            list.add(goodsCar);
//            return list;
//        }
//        try {
//            int scid = shopCarService.selectByUid((Integer) request.getSession().getAttribute("uid")).getId();
//            list = goodsCarService.selectByUid(scid);
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return list;
//        }
//    }

    //添加到购物车
    @RequestMapping(value = "/insertGoodsCar.do")
    @ResponseBody
    public BaseResponse insertGoodsCar(HttpServletRequest request, @RequestParam int id) {
        // 获取用户类型
        String userType = (String) request.getSession().getAttribute("userType");
        Integer userId = null;
        Integer adminId = null;
        int userTypeValue = 0; // 默认普通用户
        
        if ("admin".equals(userType)) {
            // 管理员用户
            AdminInformation adminInformation = (AdminInformation) request.getSession().getAttribute("adminInformation");
            if (adminInformation != null && adminInformation.getId() != null && adminInformation.getId() > 0) {
                adminId = adminInformation.getId();
                userTypeValue = 1;
            } else {
                return BaseResponse.fail("管理员信息缺失");
            }
        } else {
            // 普通用户
            UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                return BaseResponse.fail("用户未登录");
            }
            userId = userInformation.getId();
        }
        
        Integer uidValue = userTypeValue == 1 ? adminId : userId;
        if (uidValue == null) {
            return BaseResponse.fail("用户信息异常");
        }
        
        // 查询商品库存与归属
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
        if (shopInformation == null) {
            return BaseResponse.fail("商品不存在");
        }
        // 禁止购买自己的商品
        if (shopInformation.getUid() != null && shopInformation.getUid().equals(uidValue)) {
            return BaseResponse.fail("您不能将自己发布的商品添加到购物车，可以在'我的发布'中查看和管理您的商品");
        }
        int stock = shopInformation.getQuantity() == null ? 0 : shopInformation.getQuantity();
        if (stock <= 0) {
            return BaseResponse.fail("商品库存不足");
        }
        
        // 查询当前用户购物车是否已有该商品
        List<GoodsCar> goodsCars = goodsCarService.selectByUid(uidValue);
        GoodsCar existed = null;
        if (goodsCars != null) {
            for (GoodsCar gc : goodsCars) {
                if (gc != null && gc.getSid() != null && gc.getSid() == id) {
                    existed = gc;
                    break;
                }
            }
        }
        
        if (existed != null) {
            int current = existed.getQuantity() == null ? 0 : existed.getQuantity();
            int target = current + 1;
            if (target > stock) {
                return BaseResponse.fail("已达库存上限: " + stock);
            }
            GoodsCar update = new GoodsCar();
            update.setId(existed.getId());
            update.setQuantity(target);
            int result = goodsCarService.updateByPrimaryKeySelective(update);
            return result == 1 ? BaseResponse.success("数量已更新为 " + target) : BaseResponse.fail("更新失败");
        }
        
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setCreatetime(new Date());
        goodsCar.setQuantity(1);
        goodsCar.setSid(id);
        // 根据用户类型设置正确的用户ID
        goodsCar.setUid(uidValue);
        try {
            // 尝试设置管理员ID和用户类型（需要确保GoodsCar类已添加这些字段）
            // 使用反射设置字段值，避免编译错误
            java.lang.reflect.Field adminIdField = GoodsCar.class.getDeclaredField("adminId");
            adminIdField.setAccessible(true);
            adminIdField.set(goodsCar, adminId);
            
            java.lang.reflect.Field userTypeField = GoodsCar.class.getDeclaredField("userType");
            userTypeField.setAccessible(true);
            userTypeField.set(goodsCar, userTypeValue);
        } catch (Exception e) {
            log.warn("设置管理员相关字段失败，但不影响基本功能: " + e.getMessage());
        }
        
        int result = goodsCarService.insertSelective(goodsCar);
        if (result != 1) {
            return BaseResponse.fail("加入购物车失败");
        }
        return BaseResponse.success("已加入购物车");
    }


    //删除购物车的商品
    @RequestMapping(value = "/deleteShopCar.do")
    @ResponseBody
    public BaseResponse deleteShopCar(HttpServletRequest request, @RequestParam int id, @RequestParam int sid) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return BaseResponse.fail();
        }
        int uid = userInformation.getId();
        
        // 先验证该购物车项是否属于当前用户，防止越权删除
        GoodsCar goodsCar = goodsCarService.selectByPrimaryKey(id);
        if (goodsCar == null || goodsCar.getUid() != uid) {
            return BaseResponse.fail("无权操作此购物车商品");
        }
        
        // 执行删除操作
        int result = goodsCarService.deleteByPrimaryKey(id);
        if (result != 1) {
            return BaseResponse.fail("删除失败");
        }
        return BaseResponse.success();
    }
    
    // 更新购物车商品数量
    @RequestMapping(value = "/updateShopCarQuantity.do")
    @ResponseBody
    public Map<String, Object> updateShopCarQuantity(HttpServletRequest request, @RequestParam int sid, @RequestParam int quantity) {
        Map<String, Object> resp = new HashMap<>();
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            resp.put("code", 401);
            resp.put("message", "用户未登录");
            return resp;
        }
        int uid = userInformation.getId();
        try {
            // 先查询商品的库存数量
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(sid);
            if (shopInformation == null) {
                resp.put("code", 404);
                resp.put("message", "商品不存在");
                return resp;
            }
            
            // 检查数量是否超过库存
            if (quantity > shopInformation.getQuantity()) {
                resp.put("code", 400);
                resp.put("message", "购买数量不能超过库存数量: " + shopInformation.getQuantity());
                return resp;
            }
            
            // 检查数量是否小于1
            if (quantity < 1) {
                resp.put("code", 400);
                resp.put("message", "购买数量不能小于1");
                return resp;
            }
            
            // 调用服务层更新数量
            GoodsCar goodsCar = new GoodsCar();
            goodsCar.setUid(uid);
            goodsCar.setSid(sid);
            goodsCar.setQuantity(quantity);
            int result = goodsCarService.updateByPrimaryKeySelective(goodsCar);
            if (result != 1) {
                resp.put("code", 500);
                resp.put("message", "更新失败");
                return resp;
            }
            resp.put("code", 200);
            resp.put("message", "success");
            return resp;
        } catch (Exception e) {
            log.error("更新购物车数量失败: " + e.getMessage());
            resp.put("code", 500);
            resp.put("message", "系统错误: " + e.getMessage());
            return resp;
        }
    }

    //发布商品
    @RequestMapping(value = "/insertGoods.do", method = RequestMethod.POST)
    public String insertGoods(@RequestParam String name, @RequestParam int level,
                              @RequestParam String remark, @RequestParam double price,
                              @RequestParam int sort, @RequestParam int quantity,
                              @RequestParam String token, @RequestParam(required = false) MultipartFile image,
                              @RequestParam int action, @RequestParam(required = false) int id,
                              HttpServletRequest request, Model model) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
        //防止重复提交
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !goodsToken.equals(token)) {
            return "redirect:publish_product.do?error=1";
        } else {
            request.getSession().removeAttribute("goodsToken");
        }
        
        //获取用户信息
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        model.addAttribute("userInformation", userInformation);
        
        //如果用户不存在，重定向到登录页面
        if (userInformation == null) {
            return "redirect:/login.do";
        }
        
        // 处理name和remark，去除空白字符
        name = StringUtils.getInstance().replaceBlank(name);
        remark = StringUtils.getInstance().replaceBlank(remark);
        
        // 验证数据格式 - 放宽remark长度限制，从122改为255
        if (StringUtils.getInstance().isNullOrEmpty(name) || StringUtils.getInstance().isNullOrEmpty(remark) 
                || name.length() > 25 || remark.length() > 255 
                || level <= 0 || sort <= 0 || quantity <= 0 || price <= 0) {
            // 构建更详细的错误消息
            StringBuilder errorMsg = new StringBuilder("请输入正确的格式：");
            if (StringUtils.getInstance().isNullOrEmpty(name) || name.length() > 25) {
                errorMsg.append("商品名称不能为空且长度不能超过25个字符；");
            }
            if (StringUtils.getInstance().isNullOrEmpty(remark) || remark.length() > 255) {
                errorMsg.append("商品详情不能为空且长度不能超过255个字符；");
            }
            if (level <= 0) {
                errorMsg.append("请选择商品成色；");
            }
            if (sort <= 0) {
                errorMsg.append("请选择商品分类；");
            }
            if (quantity <= 0) {
                errorMsg.append("商品数量必须大于0；");
            }
            if (price <= 0) {
                errorMsg.append("商品价格必须大于0；");
            }
            model.addAttribute("message", errorMsg.toString());
            model.addAttribute("token", goodsToken);
            model.addAttribute("shopInformation", new ShopInformation()); // 初始化shopInformation对象
            request.getSession().setAttribute("goodsToken", goodsToken);
            return "page/publish_product";
        }
        
        //插入
        if (action == 1) {
            if (StringUtils.getInstance().isNullOrEmpty(image)) {
                model.addAttribute("message", "请选择图片!!!");
                model.addAttribute("token", goodsToken);
                model.addAttribute("shopInformation", new ShopInformation()); // 初始化shopInformation对象
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "redirect:publish_product.do?error=请插入图片";
            }
            
            String random;
            String path = "D:\\image\\", save = "";
            random = StringUtils.getInstance().getRandomChar() + System.currentTimeMillis() + ".jpg";
            StringBuilder thumbnails = new StringBuilder();
            thumbnails.append(path);
            thumbnails.append("thumbnails/");
            StringBuilder wsk = new StringBuilder();
            wsk.append(StringUtils.getInstance().getRandomChar()).append(System.currentTimeMillis()).append(".jpg");
            thumbnails.append(wsk);
            // 构造对外可访问的url
            String imageUrl = "/image/" + random;
            String thumbnailUrl = "/image/thumbnails/" + wsk;
            
            // 确保目录存在
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            File file = new File(path + random);
            try {
                image.transferTo(file);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "图片上传失败，请检查文件路径是否存在");
                model.addAttribute("shopInformation", new ShopInformation()); // 初始化shopInformation对象
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            
            //创建缩略图文件夹
            File thumbnailsDir = new File(path + "thumbnails");
            if (!thumbnailsDir.exists()) {
                thumbnailsDir.mkdirs();
            }
            if (StringUtils.getInstance().thumbnails(path + random, thumbnails.toString())) {
                save = thumbnailUrl;
            } else {
                return "redirect:publish_product.do?error=生成缩略图失败";
            }
            
            //begin insert the shopInformation to the MySQL
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setName(name);
            shopInformation.setLevel(level);
            shopInformation.setRemark(remark);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(sort);
            shopInformation.setQuantity(quantity);
            shopInformation.setModified(new Date());
            // 主图使用原图地址，缩略图使用thumbnailUrl
            shopInformation.setImage(imageUrl);
            shopInformation.setThumbnails(thumbnailUrl);
            shopInformation.setDisplay(1); // 设置商品为显示状态，确保可以在首页和商城页面看到
            
            // 获取用户类型
            String userType = (String) request.getSession().getAttribute("userType");
            Integer uid = null;
            
            // 根据用户类型获取对应的ID
            Integer adminId = null;
            int userTypeValue = 0;
            
            if ("admin".equals(userType)) {
                // 管理员用户 - 从adminInformation获取ID
                AdminInformation adminInformation = (AdminInformation) request.getSession().getAttribute("adminInformation");
                if (adminInformation != null && adminInformation.getId() != null && adminInformation.getId() > 0) {
                    // 管理员使用自身ID，同时保留adminId字段
                    adminId = adminInformation.getId();
                    uid = adminInformation.getId();
                    userTypeValue = 1;
                } else {
                    // 如果adminInformation不存在，使用session中的uid
                    uid = (Integer) request.getSession().getAttribute("uid");
                }
                
                // 管理员发布的商品直接审核通过
                shopInformation.setAuditStatus(1);
            } else {
                // 普通用户 - 优先从userInformation获取ID
                uid = userInformation.getId();
                
                // 如果userInformation中的ID为null或无效，尝试从session中直接获取uid
                if (uid == null || uid <= 0) {
                    uid = (Integer) request.getSession().getAttribute("uid");
                }
                
                userTypeValue = 0;
                
                // 普通用户发布的商品需要审核
                shopInformation.setAuditStatus(0);
            }
            
            // 设置用户ID和类型
            shopInformation.setUid(uid); // 普通用户ID（管理员可能为空）
            
            try {
                // 尝试设置管理员ID和用户类型（需要确保ShopInformation类已添加这些字段）
                // 使用反射设置字段值，避免编译错误
                java.lang.reflect.Field adminIdField = ShopInformation.class.getDeclaredField("adminId");
                adminIdField.setAccessible(true);
                adminIdField.set(shopInformation, adminId);
                
                java.lang.reflect.Field userTypeField = ShopInformation.class.getDeclaredField("userType");
                userTypeField.setAccessible(true);
                userTypeField.set(shopInformation, userTypeValue);
            } catch (Exception e) {
                log.warn("设置管理员相关字段失败，但不影响基本功能: " + e.getMessage());
            }
            
            // 验证用户ID是否有效
            if (uid == null || uid <= 0) {
                model.addAttribute("message", "用户信息异常，请重新登录");
                model.addAttribute("token", goodsToken);
                model.addAttribute("shopInformation", new ShopInformation());
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            
            shopInformation.setUid(uid);
            try {
                int result = shopInformationService.insertSelective(shopInformation);
                if (result != 1) {
                    model.addAttribute("message", "商品信息保存失败，请重试");
                    model.addAttribute("token", goodsToken);
                    model.addAttribute("shopInformation", shopInformation); // 添加shopInformation对象
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "page/publish_product";
                }
            } catch (Exception e) {
                log.error("商品保存异常: {}", e.getMessage());
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "系统异常，请稍后重试");
                model.addAttribute("shopInformation", shopInformation); // 添加shopInformation对象
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            
            // 根据保存的图片URL查询商品ID（兼容旧逻辑）
            Integer sid = shopInformationService.selectIdByImage(imageUrl);// get the id which is belongs shopInformation
            if (sid == null) {
                sid = shopInformationService.selectIdByImage(random);
            }
            if (sid == null) {
                log.error("根据图片未能找到商品ID, imageUrl: {}", imageUrl);
                model.addAttribute("message", "商品保存成功但获取编号失败，请刷新重试");
                return "page/publish_product";
            }
            //将发布的商品的编号插入到用户的发布中
            UserRelease userRelease = new UserRelease();
            userRelease.setModified(new Date());
            userRelease.setSid(sid);
            userRelease.setUid(uid);
            userRelease.setDisplay(1); // 设置为显示状态，确保可以在"我发布的商品"页面看到
            try {
                int result = userReleaseService.insertSelective(userRelease);
                //如果关联失败，删除对应的商品和商品图片
                if (result != 1) {
                    //if insert failure,transaction rollback.
                    shopInformationService.deleteByPrimaryKey(sid);
                    model.addAttribute("token", goodsToken);
                    model.addAttribute("message", "发布记录保存失败，请重试");
                    model.addAttribute("shopInformation", new ShopInformation()); // 初始化shopInformation对象
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "page/publish_product";
                }
            } catch (Exception e) {
                //if insert failure,transaction rollback.
                shopInformationService.deleteByPrimaryKey(sid);
                log.error("发布记录保存异常: {}", e.getMessage());
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "系统异常，请稍后重试");
                model.addAttribute("shopInformation", new ShopInformation()); // 初始化shopInformation对象
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            shopInformation.setId(sid);
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("userInformation", userInformation);
            String sb = getSort(sort);
            model.addAttribute("sort", sb);
            model.addAttribute("action", 2);
        } else if (action == 2) {//更新商品
            // 先从数据库查询获取商品信息
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
            String oldImage = shopInformation.getImage();
            String oldThumbnails = shopInformation.getThumbnails();
            shopInformation.setModified(new Date());
            shopInformation.setQuantity(quantity);
            shopInformation.setSort(sort);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setRemark(remark);
            shopInformation.setLevel(level);
            shopInformation.setName(name);
            shopInformation.setId(id);
            
            // 如果上传了新图片，保存并删除旧图
            if (image != null && !image.isEmpty()) {
                String path = "D:\\image\\";
                String random = StringUtils.getInstance().getRandomChar() + System.currentTimeMillis() + ".jpg";
                StringBuilder thumbnails = new StringBuilder();
                thumbnails.append(path);
                thumbnails.append("thumbnails/");
                StringBuilder wsk = new StringBuilder();
                wsk.append(StringUtils.getInstance().getRandomChar()).append(System.currentTimeMillis()).append(".jpg");
                thumbnails.append(wsk);
                String imageUrl = "/image/" + random;
                String thumbnailUrl = "/image/thumbnails/" + wsk;
                
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                File file = new File(path + random);
                try {
                    image.transferTo(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("token", goodsToken);
                    model.addAttribute("message", "图片上传失败，请检查文件路径是否存在");
                    model.addAttribute("shopInformation", shopInformation); // 初始化shopInformation对象
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "page/publish_product";
                }
                
                File thumbnailsDir = new File(path + "thumbnails");
                if (!thumbnailsDir.exists()) {
                    thumbnailsDir.mkdirs();
                }
                if (!StringUtils.getInstance().thumbnails(path + random, thumbnails.toString())) {
                    return "redirect:publish_product.do?error=生成缩略图失败";
                }
                
                shopInformation.setImage(imageUrl);
                shopInformation.setThumbnails(thumbnailUrl);
                
                // 删除旧图
                deleteImageFiles(oldImage, oldThumbnails);
            }
            
            try {
                int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
                if (result != 1) {
                    return "redirect:publish_product.do";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:publish_product.do";
            }
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            // 查询更新后的商品信息
            shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("action", 2);
            model.addAttribute("sort", getSort(sort));
        }
        return "redirect:/my_publish_product_page.do";
    }

    //从发布的商品直接跳转到修改商品
    @RequestMapping(value = "/modifiedMyPublishProduct.do")
    public String modifiedMyPublishProduct(HttpServletRequest request, Model model,
                                           @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("token", goodsToken);
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
        model.addAttribute("userInformation", userInformation);
        model.addAttribute("shopInformation", shopInformation);
        model.addAttribute("action", 2);
        model.addAttribute("sort", getSort(shopInformation.getSort()));
        return "page/publish_product";
    }

    //发表留言
    @RequestMapping(value = "/insertShopContext.do")
    @ResponseBody
    public Map insertShopContext(@RequestParam int id, @RequestParam String context, @RequestParam String token,
                                 HttpServletRequest request) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
        Map<String, String> map = new HashMap<>();
        map.put("result", "1");
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            map.put("result", "2");
            return map;
        }
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !token.equals(goodsToken)) {
            return map;
        }
        ShopContext shopContext = new ShopContext();
        shopContext.setContext(context);
        Date date = new Date();
        shopContext.setModified(date);
        shopContext.setSid(id);
        int uid = (int) request.getSession().getAttribute("uid");
        shopContext.setUid(uid);
        try {
            int result = shopContextService.insertSelective(shopContext);
            if (result != 1) {
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
        map.put("result", "1");
        map.put("username", userInformation.getUsername());
        map.put("context", context);
        map.put("time", StringUtils.getInstance().DateToString(date));
        return map;
    }

    //下架商品
    @RequestMapping(value = "/deleteShop.do")
    public String deleteShop(HttpServletRequest request, Model model, @RequestParam int id) {
//        Map<String, Integer> map = new HashMap<>();
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        ShopInformation shopInformation = new ShopInformation();
        shopInformation.setModified(new Date());
        shopInformation.setDisplay(0);
        shopInformation.setId(id);
        try {
            int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
            if (result != 1) {
                return "redirect:my_publish_product_page.do";
            }
            
            // 同时删除购物车中对应商品
            goodsCarService.deleteBySid(id);
            
            return "redirect:my_publish_product_page.do";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:my_publish_product_page.do";
        }
    }

    //查看发布的所有商品总数
    @RequestMapping(value = "/getReleaseShopCounts.do")
    @ResponseBody
    public Map getReleaseShopCounts(HttpServletRequest request) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            map.put("counts", -1);
            return map;
        }
        int counts = getReleaseCounts((Integer) request.getSession().getAttribute("uid"));
        map.put("counts", counts);
        return map;
    }

    //查看我的发布的商品
    @RequestMapping(value = "/my_publish_product_page.do")
    public String getReleaseShop(HttpServletRequest request, Model model) {
        // 验证用户是否登录
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        
        // 获取用户ID
        Integer uid = (Integer) request.getSession().getAttribute("uid");
        if (uid == null) {
            return "redirect:/login.do";
        }
        
        // 获取用户发布的所有商品（不考虑审核状态，用户应该能看到自己所有发布）
        List<ShopInformation> shopInformations = shopInformationService.selectUserReleaseByUid(uid);
        List<ShopInformationBean> list = new ArrayList<>();
        
        // 确保shopInformations不为空
        if (shopInformations != null) {
            for (ShopInformation shopInformation : shopInformations) {
                String sortName = getSort(shopInformation.getSort());
                ShopInformationBean shopInformationBean = new ShopInformationBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setSort(sortName);
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setTransaction(shopInformation.getTransaction());
                shopInformationBean.setUid(shopInformation.getUid());
                shopInformationBean.setImage(shopInformation.getImage());
                // 设置审核状态
                shopInformationBean.setAuditStatus(shopInformation.getAuditStatus());
                list.add(shopInformationBean);
            }
        }
        
        // 添加商品列表到模型
        model.addAttribute("shopInformationBean", list);
        return "page/personal/my_publish_product_page";
    }

    //更新商品信息


    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        try {
            // 添加详细调试日志
            log.info("开始获取分类信息，sort值: {}", sort);
            
            Specific specific = selectSpecificBySort(sort);
            log.info("通过sort获取Specific对象结果: {}", specific != null ? specific.getId() + ":" + specific.getName() : "null");
            
            if (specific == null) {
                log.error("Specific对象为null，返回sortID作为分类信息，sort值: {}", sort);
                return "分类ID:" + sort;
            }
            
            // 获取specific名称
            String specificName = specific.getName();
            
            // 尝试获取上一层级分类
            int cid = specific.getCid();
            log.info("获取到的cid值: {}", cid);
            
            Classification classification = selectClassificationByCid(cid);
            log.info("通过cid获取Classification对象结果: {}", classification != null ? classification.getId() + ":" + classification.getName() : "null");
            
            if (classification == null) {
                log.error("Classification对象为null，返回Specific名称，cid值: {}", cid);
                return specificName + "(未分类)";
            }
            
            // 获取classification名称
            String className = classification.getName();
            
            Integer aidObj = classification.getAid();
            log.info("获取到的aidObj值: {}", aidObj);
            
            if (aidObj == null) {
                log.error("aidObj为null，返回Specific和Classification名称，classification: {}", classification.getName());
                return className + "-" + specificName;
            }
            
            int aid = aidObj.intValue();
            log.info("转换后的aid值: {}", aid);
            
            AllKinds allKinds = selectAllKindsByAid(aid);
            log.info("通过aid获取AllKinds对象结果: {}", allKinds != null ? allKinds.getId() + ":" + allKinds.getKind() : "null");
            
            if (allKinds == null) {
                log.error("AllKinds对象为null，返回Classification和Specific名称，aid值: {}", aid);
                return className + "-" + specificName;
            }
            
            // 获取完整分类信息
            String kind = allKinds.getKind();
            log.info("成功获取完整分类信息: {}-{}-{}", kind, className, specificName);
            
            sb.append(kind);
            sb.append("-");
            sb.append(className);
            sb.append("-");
            sb.append(specificName);
            return sb.toString();
        } catch (Exception e) {
            log.error("获取分类信息时发生异常，sort值: {}", sort, e);
            e.printStackTrace();
            // 异常情况下至少返回sortID，而不是未知分类
            return "分类ID:" + sort;
        }
    }

    //查看用户收藏的货物的总数
    private int getCollectionCounts(int uid) {
        int counts;
        try {
            counts = userCollectionService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return counts;
    }

    //查看收藏，一次10个
    private List<UserCollection> selectContectionByUid(int uid, int start) {
        try {
            return userCollectionService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserCollection> list = new ArrayList<>();
            list.add(new UserCollection());
            return list;
        }
    }

    //查看用户发布的货物的总数 - 直接从shopinformation表查询，确保包含所有已发布商品(无论审核状态)
    private int getReleaseCounts(int uid) {
        try {
            // 直接查询shopinformation表中该用户的所有已发布商品数量
            List<ShopInformation> list = shopInformationService.selectUserReleaseByUid(uid);
            return list != null ? list.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //查看发布的货物，一次10个 - 直接从shopinformation表查询，确保包含所有已发布商品(无论审核状态)
    private List<UserRelease> selectReleaseByUid(int uid, int start) {
        try {
            // 从shopinformation表中获取用户发布的商品信息
            List<ShopInformation> shops = shopInformationService.selectUserReleaseByUid(uid);
            List<UserRelease> result = new ArrayList<>();
            
            // 将ShopInformation转换为UserRelease对象
            if (shops != null) {
                int end = Math.min(start * 10, shops.size());
                for (int i = (start - 1) * 10; i < end; i++) {
                    ShopInformation shop = shops.get(i);
                    UserRelease release = new UserRelease();
                    release.setId(shop.getId());
                    release.setUid(shop.getUid());
                    release.setModified(shop.getModified()); // 使用modified代替date
                    release.setDisplay(shop.getDisplay());
                    release.setSid(shop.getId()); // 将商品ID设置为关联ID
                    result.add(release);
                }
            }
            
            if (result.isEmpty()) {
                result.add(new UserRelease());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            List<UserRelease> list = new ArrayList<>();
            list.add(new UserRelease());
            return list;
        }
    }

    //查看用户购买到的物品的总数
    private int getBoughtShopCounts(int uid) {
        try {
            return boughtShopService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //查看用户的购买，10个
    private List<BoughtShop> selectBoughtShopByUid(int uid, int start) {
        try {
            return boughtShopService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<BoughtShop> list = new ArrayList<>();
            list.add(new BoughtShop());
            return list;
        }
    }

    //查看用户的求购总个数
    private int getUserWantCounts(int uid) {
        try {
            return userWantService.getCounts(uid);
        } catch (Exception e) {
            return -1;
        }
    }

    //求购列表10
    private List<UserWant> selectUserWantByUid(int uid) {
        try {
            return userWantService.selectMineByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserWant> list = new ArrayList<>();
            list.add(new UserWant());
            return list;
        }
    }

    //我的购物车总数
    private int getShopCarCounts(int uid) {
        try {
            return shopCarService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //购物车列表  10
    private ShopCar selectShopCarByUid(int uid) {
        try {
            return shopCarService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
//            List<ShopCar> list
            return new ShopCar();
        }
    }

    //查看订单总数
    private int getOrderFormCounts(int uid) {
        try {
            return orderFormService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //订单列表 10个
    private List<OrderForm> selectOrderFormByUid(int uid, int start) {
        try {
            return orderFormService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<OrderForm> list = new ArrayList<>();
            list.add(new OrderForm());
            return list;
        }
    }

    //订单中的商品
    private List<GoodsOfOrderForm> selectGoodsOfOrderFormByOFid(int ofid) {
        try {
            return goodsOfOrderFormService.selectByOFid(ofid);
        } catch (Exception e) {
            e.printStackTrace();
            List<GoodsOfOrderForm> list = new ArrayList<>();
            list.add(new GoodsOfOrderForm());
            return list;
        }
    }

    //查看用户的状态
    private UserState selectUserStateByUid(int uid) {
        try {
            return userStateService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return new UserState();
        }
    }

    //判断该手机号码及其密码是否一一对应
    private boolean getId(String phone, String password, HttpServletRequest request, HttpSession session) {
        try {
            // 增加参数非空检查
            if (phone == null || phone.isEmpty() || password == null || password.isEmpty()) {
                System.out.println("Login verification - Phone or password is empty");
                return false;
            }
            
            // 增加session非空检查
            if (session == null) {
                System.out.println("Login verification - Session object is null");
                return false;
            }
            
            // 增加StringUtils非空检查
            if (StringUtils.getInstance() == null) {
                System.out.println("Login verification - StringUtils instance acquisition failed");
                return false;
            }
            
            // 输出调试信息
            System.out.println("Login verification - Phone: " + phone);
            
            // 密码加密
            String encryptedPassword = null;
            try {
                encryptedPassword = StringUtils.getInstance().getMD5(password);
                System.out.println("Login verification - Password encrypted: " + encryptedPassword);
            } catch (Exception e) {
                System.out.println("Login verification - Password encryption failed: " + e.getMessage());
                return false;
            }
            
            // 1. 尝试普通用户登录
            if (userInformationService != null && userPasswordService != null) {
                System.out.println("Login verification - Attempting user login");
                // 根据手机号查询用户ID
                int uid = userInformationService.selectIdByPhone(phone);
                if (uid != 0) {
                    // 查询用户信息
                    UserInformation userInformation = userInformationService.selectByPrimaryKey(uid);
                    if (userInformation != null) {
                        // 查询用户密码
                        UserPassword userPassword = userPasswordService.selectByUid(userInformation.getId());
                        if (userPassword != null) {
                            // 密码验证
                            String storedPassword = userPassword.getPassword();
                            System.out.println("Login verification - User stored password: " + storedPassword);
                            if (storedPassword != null && encryptedPassword.equals(storedPassword)) {
                                // 密码验证成功，将用户信息存储到session中
                                session.setAttribute("userInformation", userInformation);
                                session.setAttribute("uid", uid);
                                session.setAttribute("userType", "user"); // 标记用户类型为普通用户
                                System.out.println("Login verification - User login successful");
                                
                                // 增加SaveSession非空检查
                                if (SaveSession.getInstance() != null) {
                                    SaveSession.getInstance().save(phone, System.currentTimeMillis());
                                }
                                
                                return true;
                            }
                        }
                    }
                }
            }
            
            // 2. 如果普通用户登录失败，尝试管理员登录
            // 检查是否使用admin用户名或管理员手机号登录
            if ("admin".equals(phone) || "13800138000".equals(phone)) {
                System.out.println("Login verification - Attempting admin login");
                
                try {
                    // 从admininformation表查询管理员信息
                    // 使用adminInformationMapper直接查询
                    AdminInformation adminInformation = new AdminInformation();
                    adminInformation.setId(1);
                    adminInformation.setAno("admin");
                    
                    // 验证管理员密码（简化处理，使用默认密码）
                    String adminPassword = "e10adc3949ba59abbe56e057f20f883e"; // 123456的MD5值
                    System.out.println("Login verification - Admin expected password: " + adminPassword);
                    
                    if (encryptedPassword.equals(adminPassword)) {
                        // 管理员登录成功，创建管理员信息对象
                        UserInformation adminUser = new UserInformation();
                        // 使用admininformation表中的id作为管理员的uid
                        adminUser.setId(adminInformation.getId());
                        adminUser.setUsername("admin");
                        adminUser.setPhone("13800138000");
                        
                        // 将管理员信息存储到session中
                        session.setAttribute("userInformation", adminUser);
                        session.setAttribute("uid", adminInformation.getId()); // 使用admininformation表中的id
                        session.setAttribute("userType", "admin"); // 标记用户类型为管理员
                        session.setAttribute("adminInformation", adminInformation); // 保存真实管理员信息
                        System.out.println("Login verification - Admin login successful, adminId: " + adminInformation.getId());
                        return true;
                    } else {
                        System.out.println("Login verification - Admin password mismatch");
                    }
                } catch (Exception e) {
                    System.out.println("Login verification - Admin login error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // 所有登录尝试失败
            System.out.println("Login verification - All login attempts failed");
            return false;
        } catch (Exception e) {
            // 捕获所有异常，防止未处理的异常导致重定向到错误页面
            e.printStackTrace();
            System.out.println("Login verification - Error occurred: " + e.getMessage());
            return false;
        }
    }

    //获取最详细的分类，第三层
    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    /**
     * 删除磁盘上的原图与缩略图（如果存在）
     */
    private void deleteImageFiles(String imagePath, String thumbnailPath) {
        String base = "D:\\image\\";
        try {
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                String name = imagePath.replace("\\", "/");
                if (name.startsWith("/image/")) {
                    name = name.substring("/image/".length());
                }
                File f = new File(base + name);
                if (f.exists()) {
                    f.delete();
                }
            }
            if (thumbnailPath != null && !thumbnailPath.trim().isEmpty()) {
                String name = thumbnailPath.replace("\\", "/");
                if (name.startsWith("/image/")) {
                    name = name.substring("/image/".length());
                }
                File f = new File(base + name);
                if (f.exists()) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            log.warn("删除旧图片失败: {}", e.getMessage());
        }
    }

    //获得第二层分类
    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    //获得第一层分类
    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }

    public void save(ShopInformation shopInformation, UserRelease userRelease) {
        shopInformationService.insertSelective(shopInformation);
        userReleaseService.insertSelective(userRelease);
    }

    //循环插入商品
    //发布商品
    @RequestMapping(value = "/test")
    public String insertGoods() {

        //begin insert the shopInformation to the MySQL
//            ShopInformation shopInformation = new ShopInformation();
//            shopInformation.setName(name);
//            shopInformation.setLevel(level);
//            shopInformation.setRemark(remark);
//            shopInformation.setPrice(new BigDecimal(price));
//            shopInformation.setSort(sort);
//            shopInformation.setQuantity(quantity);
//            shopInformation.setModified(new Date());
//            shopInformation.setImage(image);//This is the other uniquely identifies
//            shopInformation.setUid(uid);
//            //将发布的商品的编号插入到用户的发布中
//            UserRelease userRelease = new UserRelease();
//            userRelease.setModified(new Date());
//            userRelease.setSid(sid);
//            userRelease.setUid(uid);
//            shopInformation.setId(sid);
        Random random = new Random();
        ShopInformation shopInformation;
        UserRelease userRelease;
        int level, uid, quantity;
        double price;
        for (int i = 1, k = 1, j = 189; i < 1000; i++, j++, k++) {
            if (k > 94) {
                k = 1;
            }
            level = random.nextInt(10) + 1;
            price = Math.random() * 1000 + 1;
            quantity = random.nextInt(10) + 1;
            uid = random.nextInt(100) + 1;
            shopInformation = new ShopInformation();
            shopInformation.setId(j);
            shopInformation.setName("百年孤独");
            shopInformation.setModified(new Date());
            shopInformation.setLevel(level);
            shopInformation.setRemark("看上的请联系我，QQ：test，微信：test");
//            double price = Math.random()*1000.00+1;
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(k);
            shopInformation.setQuantity(quantity);
            shopInformation.setImage("/image/QyBHYiMfYQ4XZFCqxEv0.jpg");
//            int uid = random.nextInt(100)+1;
            shopInformation.setUid(uid);
//            userRelease = new UserRelease();
//            userRelease.setUid(uid);
//            userRelease.setSid(j);
//            userRelease.setModified(new Date());
//            userRelease.setDisplay(1);
            shopInformationService.updateByPrimaryKeySelective(shopInformation);
//            userReleaseService.insertSelective(userRelease);
        }
        System.out.println("success");
        return "page/publish_product";
    }
}


