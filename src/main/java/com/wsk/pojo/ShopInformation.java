package com.wsk.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ShopInformation implements Serializable {
    private Integer id;

    private Date modified;

    private String name;

    private Integer level;

    private String remark;

    private BigDecimal price;

    private Integer sort;

    private Integer display;

    private Integer quantity;

    private Integer transaction;

    private Integer uid;

    private String image;

    private Integer sales;

    private String thumbnails;

    private Integer auditStatus; // 审核状态：0-待审核，1-审核通过，2-审核拒绝

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort ;
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTransaction() {
        return transaction;
    }

    public void setTransaction(Integer transaction) {
        this.transaction = transaction;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getImage() {
        // 兼容历史数据：旧数据存的是文件名，新数据是 /image/ 前缀
        if (image == null || image.trim().isEmpty()) {
            return image;
        }
        String val = image.trim();
        if (val.startsWith("/") || val.startsWith("http")) {
            return val;
        }
        return "/image/" + val;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getThumbnails() {
        if (thumbnails == null || thumbnails.trim().isEmpty()) {
            return thumbnails;
        }
        String val = thumbnails.trim();
        if (val.startsWith("/") || val.startsWith("http")) {
            return val;
        }
        return "/image/thumbnails/" + val;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }
}