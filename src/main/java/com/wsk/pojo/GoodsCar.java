package com.wsk.pojo;

import java.io.Serializable;
import java.util.Date;

public class GoodsCar implements Serializable {
    private Integer id;

    private Date createtime;

    private Integer sid;

    private Integer uid;

    private Integer quantity;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime == null ? null : (Date) createtime.clone();
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime == null ? null : (Date) createtime.clone();
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }



    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}