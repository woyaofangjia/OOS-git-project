package com.wsk.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息实体类
 * 存储用户的基本信息，如用户名、手机号、班级等
 */
public class UserInformation implements Serializable {
    /**
     * 用户ID，主键
     */
    private Integer id;

    /**
     * 最后修改时间
     */
    private Date modified;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 班级
     */
    private String clazz;

    /**
     * 学号
     */
    private String sno;

    /**
     * 宿舍
     */
    private String dormitory;

    /**
     * 性别
     */
    private String gender;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getModified() {
        return modified == null ? null : (Date) modified.clone();
    }

    public void setModified(Date modified) {
        this.modified = modified == null ? null : (Date) modified.clone();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz == null ? null : clazz.trim();
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno == null ? null : sno.trim();
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory == null ? null : dormitory.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

    public Date getCreatetime() {
        return createtime == null ? null : (Date) createtime.clone();
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime == null ? null : (Date) createtime.clone();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }
}