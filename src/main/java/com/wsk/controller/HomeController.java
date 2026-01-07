package com.wsk.controller;

import com.wsk.bean.ShopInformationBean;
import com.wsk.pojo.*;
import com.wsk.service.*;
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
 * Created by wsk1103 on 2017/5/11.
 */
@Controller
public class HomeController {
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private ShopContextService shopContextService;


    @RequestMapping(value = {"/", "/home.do"})
    public String home(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        // if user login,the session will have the "userInformation"
        if (!StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            model.addAttribute("userInformation", userInformation);
            // 为了确保登录后页面不为空，添加一个登录成功的标记
            model.addAttribute("loginSuccess", true);
        } else {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        }
        //一般形式进入首页
        try {
            List<ShopInformation> shopInformations = selectTen(1, 5);
            List<ShopInformationBean> list = new ArrayList<>();
            int counts = getShopCounts();
            model.addAttribute("shopInformationCounts", counts);
            String stringBuffer;
            for (ShopInformation shopInformation : shopInformations) {
                stringBuffer = getSortName(shopInformation.getSort());
                ShopInformationBean shopInformationBean = new ShopInformationBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setSort(stringBuffer);
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setUid(shopInformation.getUid());
                shopInformationBean.setTransaction(shopInformation.getTransaction());
                shopInformationBean.setImage(shopInformation.getImage());
                list.add(shopInformationBean);
            }
            model.addAttribute("shopInformationBean", list);
        } catch (Exception e) {
            // 添加更详细的异常日志
            System.out.println("获取商品数据时出错: " + e.getMessage());
            e.printStackTrace();
            // 即使出错也返回index页面，而不是重定向到登录页面
            model.addAttribute("errorMessage", "加载商品数据时出错");
        }
        return "index";
    }

    //进入商城
    @RequestMapping(value = "/mall_page.do")
    public String mallPage(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        try {
            List<ShopInformation> shopInformations = selectTen(1, 12);
            List<ShopInformationBean> list = new ArrayList<>();
            int counts = getShopCounts();
            model.addAttribute("shopInformationCounts", counts);
            String sortName;
            for (ShopInformation shopInformation : shopInformations) {
                int sort = shopInformation.getSort();
                sortName = getSortName(sort);
                ShopInformationBean shopInformationBean = new ShopInformationBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setSort(sortName);
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setTransaction(shopInformation.getTransaction());
                shopInformationBean.setUid(shopInformation.getUid());
                shopInformationBean.setImage(shopInformation.getImage());
                list.add(shopInformationBean);
            }
            model.addAttribute("shopInformationBean", list);
        } catch (Exception e) {
            // 添加更详细的异常日志
            System.out.println("加载商城页面数据时出错: " + e.getMessage());
            e.printStackTrace();
            // 设置错误消息，而不是重定向到登录页面
            model.addAttribute("errorMessage", "加载商城数据时出错");
            model.addAttribute("shopInformationBean", new ArrayList<>()); // 添加空列表防止页面渲染错误
        }
        return "page/mall_page";
    }

    //通过分类的第三层id获取全名
    private String getSortName(int sort) {
        StringBuilder stringBuffer = new StringBuilder();
        try {
            Specific specific = selectSpecificBySort(sort);
            if (specific == null) {
                return "未知分类";
            }
            int cid = specific.getCid();
            Classification classification = selectClassificationByCid(cid);
            if (classification == null) {
                return "未知分类";
            }
            Integer aidObj = classification.getAid();
            if (aidObj == null) {
                return "未知分类";
            }
            int aid = aidObj.intValue();
            AllKinds allKinds = selectAllKindsByAid(aid);
            if (allKinds == null) {
                return "未知分类";
            }
            stringBuffer.append(allKinds.getKind());
            stringBuffer.append("-");
            stringBuffer.append(classification.getName());
            stringBuffer.append("-");
            stringBuffer.append(specific.getName());
            return stringBuffer.toString();
        } catch (Exception e) {
            // 捕获异常，防止空指针异常导致页面错误
            e.printStackTrace();
            return "未知分类";
        }
    }

    //获得分类中的第一层
    @RequestMapping(value = "/getAllKinds.do")
    @ResponseBody
    public List<AllKinds> getAllKind() {
        return getAllKinds();
    }

    //获得分类中的第二层，通过第一层的pid
    @RequestMapping(value = "/getClassification.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<Classification> getClassificationByPid(@RequestParam int pid) {
        // 使用classificationService.selectByPid直接获取，现在这个方法会返回包含三级分类的对象
        return classificationService.selectByPid(pid);
    }

    //通过第二层的id获取对应的第三层，支持GET和POST请求
    @RequestMapping(value = "/getSpecific.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<Specific> getSpecificByCid(@RequestParam int id) {
        return selectAllSpecific(id);
    }

    //get the shops counts
    @RequestMapping(value = "/getShopsCounts.do")
    @ResponseBody
    public Map getShopsCounts() {
        Map<String, Integer> map = new HashMap<>();
        int counts = 0;
        try {
            counts = shopInformationService.getCounts();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("counts", counts);
            return map;
        }
        map.put("counts", counts);
        return map;
    }

    @RequestMapping(value = "/getShops.do")
    @ResponseBody
    public List getShops(@RequestParam int start) {
        List<ShopInformation> list = new ArrayList<>();
        try {
            int end = 12;
            list = selectTen(start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }


    //获取商品，分页,一次性获取end个
    private List<ShopInformation> selectTen(int start, int end) {
        Map map = new HashMap();
        map.put("start", (start - 1) * end);
        map.put("end", end);
        List<ShopInformation> list = shopInformationService.selectTen(map);
        return list;
    }

    //获取最详细的分类，第三层
    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    //获得第二层分类
    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    //获得第一层分类
    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }

    //获得第一层所有
    private List<AllKinds> getAllKinds() {
        return allKindsService.selectAll();
    }

    //根据第一层的id获取该层下的第二层
    private List<Classification> selectAllClassification(int pid) {
        return classificationService.selectByPid(pid);
    }

    //根据第二层的id获取其对应的第三层所有id
    private List<Specific> selectAllSpecific(int cid) {
        return specificeService.selectByCid(cid);
    }

    //获得商品总页数
    private int getShopCounts() {
        return shopInformationService.getCounts();
    }

    //获得商品留言总页数
    private int getShopContextCounts(int sid) {
        return shopContextService.getCounts(sid);
    }

    //获得商品留言，10条
    private List<ShopContext> selectShopContextBySid(int sid, int start) {
        return shopContextService.findById(sid, (start - 1) * 10);
    }
}
