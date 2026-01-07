package com.wsk.tool;

import net.coobird.thumbnailator.Thumbnails;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 提供字符串处理、验证、加密等常用方法
 */
public class StringUtils {

    /**
     * 静态内部类，用于实现单例模式的懒加载
     */
    private static class LayHolder {
        /**
         * 单例实例
         */
        private static final StringUtils instance = new StringUtils();
    }

    /**
     * 私有构造方法，防止外部实例化
     */
    private StringUtils() {
    }

    /**
     * 获取工具类实例
     * @return StringUtils单例实例
     */
    public static StringUtils getInstance() {
        return LayHolder.instance;
    }

    /**
     * 去除字符串中的空白字符（空格、制表符、换行符等）
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 生成随机字符串（10位，包含大小写字母和数字）
     * @return 随机字符串
     */
    public String getRandomChar() {
        Random random = new Random();
        String s = "qw2ert1yui6opa7s3df9ghj5klz0x4cv8bnmQWERTYUIOPASDFGHJKLZXCVBNM";
        StringBuffer stringBuffer = new StringBuffer();
        char[] chars = s.toCharArray();
        for (int i = 0; i < 10; i++) {
            stringBuffer.append(chars[random.nextInt(s.length())]);
        }
        return stringBuffer.toString();
    }

    /**
     * 将Date对象格式化为字符串
     * @param date 日期对象
     * @return 格式化后的日期字符串，格式：yyyy-MM-dd HH:mm:ss
     */
    public String DateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = simpleDateFormat.format(date);
        return result;
    }

    /**
     * 验证手机号格式是否正确
     * @param phone 手机号
     * @return 是否为有效的手机号格式
     */
    public boolean isPhone(String phone) {
        // 更新正则表达式以支持所有常见的中国手机号段
        Pattern p = Pattern.compile("^1[3-9]\\d{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 对字符串进行MD5加密
     * @param str 待加密的字符串
     * @return MD5加密后的字符串（32位小写）
     */
    public String getMD5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断对象是否为null或空
     * 支持：null、字符串、集合、Map、数组等类型的判断
     * @param obj 待判断的对象
     * @return 是否为null或空
     */
    public boolean isNullOrEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;

        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map) obj).isEmpty();

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    /**
     * 读取指定文本文件内容
     * @return 文本文件内容列表
     * @throws IOException IO异常
     */
    public ArrayList readTxt() throws IOException {
        ArrayList<String> list = new ArrayList<>();
        String encoding = "GBK";
        File file = new File("D:\\image\\txt\\all.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String txt;
        while ((txt = bufferedReader.readLine()) != null) {
            list.add(txt);
        }
        reader.close();
        return list;
    }

    /**
     * 根据敏感词列表过滤文本内容
     * 将敏感词替换为**
     * @param test 待过滤的文本
     * @return 过滤后的文本
     */
    public String txtReplace(String test) {
        try {
            ArrayList<String> list = readTxt();
            test = test.replaceAll("\\s*|\t|\r|\n", "");
            for (String aList : list) {
                test = test.replaceAll(aList, "**");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return test;
    }

    /**
     * 生成图片缩略图
     * @param path 原图路径
     * @param save 缩略图保存路径
     * @return 是否生成成功
     */
    public boolean thumbnails(String path, String save) {
        try {
            Thumbnails.of(path).size(215, 229).toFile(save);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
