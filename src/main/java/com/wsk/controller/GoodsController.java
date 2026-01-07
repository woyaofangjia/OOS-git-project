package com.wsk.controller;

import com.wsk.bean.ShopContextBean;
import com.wsk.bean.ShopInformationBean;
import com.wsk.bean.UserWantBean;
import com.wsk.pojo.*;
import com.wsk.service.*;
import com.wsk.token.TokenProccessor;
import com.wsk.tool.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器，处理商品相关的请求
 * 负责商品的发布、查询、详情查看等功能
 */
@Controller
public class GoodsController {
    /**
     * 商品信息服务
     */
    @Resource
    private ShopInformationService shopInformationService;
    
    /**
     * 商品详情服务
     */
    @Resource
    private ShopContextService shopContextService;
    
    /**
     * 用户信息服务
     */
    @Resource
    private UserInformationService userInformationService;
    
    /**
     * 商品规格服务
     */
    @Resource
    private SpecificeService specificeService;
    
    /**
     * 商品分类服务
     */
    @Resource
    private ClassificationService classificationService;
    
    /**
     * 全部分类服务
     */
    @Resource
    private AllKindsService allKindsService;
    
    /**
     * 用户求购服务
     */
    @Resource
    private UserWantService userWantService;

    /**
     * 发布商品页面
     * @param request HttpServletRequest对象
     * @param model Model对象，用于传递数据到视图
     * @return 页面路径
     */
    @RequestMapping(value = "/publish_product.do", method = RequestMethod.GET)
    public String publish(HttpServletRequest request, Model model) {
        //检查是否是管理员登录（通过userType标识）
        String userType = (String) request.getSession().getAttribute("userType");
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        
        if ("admin".equals(userType) && userInformation != null) {
            //如果是管理员登录，直接跳过认证检查
            model.addAttribute("userInformation", userInformation);
        } else {
            //判断普通用户有没有登录
            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                //如果没有登录
                return "redirect:/login.do";
            } else {
                model.addAttribute("userInformation", userInformation);
                //如果是普通用户登录，判断该用户有没有经过认证
                try {
                    String realName = userInformation.getRealname();
                    String sno = userInformation.getSno();
                    String dormitory = userInformation.getDormitory();
                    if (StringUtils.getInstance().isNullOrEmpty(realName) || StringUtils.getInstance().isNullOrEmpty(sno) || StringUtils.getInstance().isNullOrEmpty(dormitory)) {
                        //没有
                        model.addAttribute("message", "请先认证真实信息");
                        return "redirect:personal_info.do?redirectUrl=publish_product.do";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/login.do";
                }
            }
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("shopInformation", new ShopInformation());
        model.addAttribute("action", 1);
        model.addAttribute("token", goodsToken);
        return "page/publish_product";
    }

    /**
     * 模糊查询商品
     * @param request HttpServletRequest对象
     * @param model Model对象，用于传递数据到视图
     * @param name 商品名称关键字
     * @return 页面路径
     */
    @RequestMapping(value = "/findShopByName.do")
    public String findByName(HttpServletRequest request, Model model,
                             @RequestParam String name) {
        try {
            List<ShopInformation> shopInformations = shopInformationService.selectByName(name);
            UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                userInformation = new UserInformation();
                model.addAttribute("userInformation", userInformation);
            } else {
                model.addAttribute("userInformation", userInformation);
            }
            List<ShopInformationBean> shopInformationBeans = new ArrayList<>();
            String sortName;
            for (ShopInformation shopInformation : shopInformations) {
                int sort = shopInformation.getSort();
                sortName = getSort(sort);
                ShopInformationBean shopInformationBean = new ShopInformationBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setTransaction(shopInformation.getTransaction());
                shopInformationBean.setSort(sortName);
                shopInformationBean.setUid(shopInformation.getUid());
                shopInformationBean.setImage(shopInformation.getImage());
                shopInformationBeans.add(shopInformationBean);
            }
            model.addAttribute("shopInformationBean", shopInformationBeans);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:mall_page.do";
        }
        return "page/mall_page";
    }

    //进入查看商品详情
    /**
     * 根据ID查询商品详情
     * @param id 商品ID
     * @param request HttpServletRequest对象
     * @param model Model对象，用于传递数据到视图
     * @return 页面路径
     */
    @RequestMapping(value = "/selectById.do")
    public String selectById(@RequestParam int id,
                              HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        }
        try {
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("shopInformation", shopInformation);
            List<ShopContext> shopContexts = shopContextService.selectById(id);
            List<ShopContextBean> shopContextBeans = new ArrayList<>();
            for (ShopContext s : shopContexts) {
                ShopContextBean shopContextBean = new ShopContextBean();
                UserInformation u = userInformationService.selectByPrimaryKey(s.getUid());
                shopContextBean.setContext(s.getContext());
                shopContextBean.setId(s.getId());
                shopContextBean.setModified(s.getModified());
                shopContextBean.setUid(u.getId());
                shopContextBean.setUsername(u.getUsername());
                shopContextBeans.add(shopContextBean);
            }
            String sort = getSort(shopInformation.getSort());
            String goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("sort", sort);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopContextBeans", shopContextBeans);
            return "page/product_info";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    //进入到求购商城
    /**
     * 求购商城页面
     * @param request HttpServletRequest对象
     * @param model Model对象，用于传递数据到视图
     * @return 页面路径
     */
    @RequestMapping(value = "/require_mall.do")
    public String requireMall(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
        }
        model.addAttribute("userInformation", userInformation);
        
        try {
            // 查询所有已显示的求购信息
            List<UserWant> userWants = userWantService.selectAll();
            List<UserWantBean> list = new ArrayList<>();
            
            // 安全检查，确保userWants不为null
            if (userWants != null && !userWants.isEmpty()) {
                for (UserWant userWant : userWants) {
                    // 确保userWant不为null且display为1（已通过审核或管理员发布）
                    if (userWant != null && userWant.getDisplay() == 1) {
                        UserWantBean u = new UserWantBean();
                        // 使用安全的getSort方法获取分类名称（已包含null检查）
                        u.setSort(getSort(userWant.getSort()));
                        
                        // 安全设置其他属性，避免NullPointerException
                        if (userWant.getRemark() != null) {
                            u.setRemark(userWant.getRemark());
                        }
                        if (userWant.getQuantity() != null) {
                            u.setQuantity(userWant.getQuantity());
                        }
                        if (userWant.getPrice() != null) {
                            u.setPrice(userWant.getPrice().doubleValue());
                        }
                        if (userWant.getUid() != null) {
                            u.setUid(userWant.getUid());
                        }
                        if (userWant.getId() != null) {
                            u.setId(userWant.getId());
                        }
                        if (userWant.getCreatetime() != null) {
                            u.setCreatetime(userWant.getCreatetime());
                        }
                        if (userWant.getName() != null) {
                            u.setName(userWant.getName());
                        }
                        
                        list.add(u);
                    }
                }
            }
            model.addAttribute("list", list);
        } catch (Exception e) {
            // 使用更详细的异常记录
            System.err.println("求购商城页面加载异常: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("list", new ArrayList<>());
        }
        return "page/require_mall";
    }

    //通过id查看商品的详情
    /**
     * 根据ID查询商品信息（AJAX）
     * @param id 商品ID
     * @return 商品信息对象
     */
    @RequestMapping(value = "/findShopById.do")
    @ResponseBody
    public ShopInformation findShopById(@RequestParam int id) {
        return shopInformationService.selectByPrimaryKey(id);
    }

    //通过分类选择商品
    /**
     * 根据分类查询商品（AJAX）
     * @param sort 分类ID
     * @return 商品列表
     */
    @RequestMapping(value = "/selectBySort.do")
    @ResponseBody
    public List<ShopInformation> selectBySort(@RequestParam int sort) {
        return shopInformationService.selectBySort(sort);
    }

    //分页查询
    /**
     * 根据数量查询商品（AJAX）
     * @param counts 查询数量
     * @return 商品列表
     */
    @RequestMapping(value = "/selectByCounts.do")
    @ResponseBody
    public List<ShopInformation> selectByCounts(@RequestParam int counts) {
        Map<String, Integer> map = new HashMap<>();
        map.put("start", (counts - 1) * 12);
        map.put("end", 12);
        return shopInformationService.selectTen(map);
    }
//    //通过id查看商品详情
//    @RequestMapping(value = "/showShop")
//    public String showShop(@RequestParam int id, HttpServletRequest request, Model model) {
//        ShopInformation shopInformation =
//    }

    //获取最详细的分类，第三层
    /**
     * 根据分类ID查询规格信息
     * @param sort 分类ID
     * @return 规格信息对象
     */
    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    //获得第二层分类
    /**
     * 根据分类ID查询分类信息
     * @param cid 分类ID
     * @return 分类信息对象
     */
    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    //获得第一层分类
    /**
     * 根据种类ID查询种类信息
     * @param aid 种类ID
     * @return 种类信息对象
     */
    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }

    /**
     * 获取分类名称
     * @param sort 分类ID
     * @return 分类名称
     */
    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        try {
            Specific specific = selectSpecificBySort(sort);
            if (specific == null) {
                return "分类ID:" + sort;
            }
            int cid = specific.getCid();
            Classification classification = selectClassificationByCid(cid);
            if (classification == null) {
                return specific.getName() + "(未分类)";
            }
            Integer aidObj = classification.getAid();
            String className = classification.getName();
            String specificName = specific.getName();
            
            // 如果aid为null或查询allKinds失败，仍然返回已有分类信息
            if (aidObj == null) {
                System.out.println("aidObj为null，返回Specific和Classification名称，classification: " + className);
                return className + "-" + specificName;
            }
            
            int aid = aidObj;
            AllKinds allKinds = selectAllKindsByAid(aid);
            if (allKinds == null) {
                return className + "-" + specificName;
            }
            
            String allName = allKinds.getKind();
            sb.append(allName);
            sb.append("-");
            sb.append(className);
            sb.append("-");
            sb.append(specificName);
            return sb.toString();
        } catch (Exception e) {
            System.out.println("获取分类信息异常，返回分类ID: " + sort + ", 异常: " + e.getMessage());
            e.printStackTrace();
            return "分类ID:" + sort;
        }
    }

}
