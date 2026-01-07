package com.wsk.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户密码实体类
 * 存储用户的密码信息，与用户信息表关联
 */
public class UserPassword implements Serializable {
    /**
     * 密码ID，主键
     */
    private Integer id;

    /**
     * 最后修改时间
     */
    private Date modified;

    /**
     * 密码（MD5加密存储）
     */
    private String password;

    /**
     * 用户ID，外键关联用户信息表
     */
    private Integer uid;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}