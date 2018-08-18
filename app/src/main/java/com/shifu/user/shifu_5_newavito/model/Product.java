package com.shifu.user.shifu_5_newavito.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

import io.realm.RealmObject;


public class Product extends RealmObject implements MyRealms{

    private final static String FIELD_ID = "upid";

    //PrimaryKey
    private Long upid;

    @SerializedName("date")
    @Expose
    private Date date;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("nameCategory")
    @Expose
    private String nameCategory;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("cost")
    @Expose
    private Long cost;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("likes")
    @Expose
    private Long likes;

    public Product() {
    }

    public Product(String username, String title, String mobile, String nameCategory, String description, Long cost, String location) {
        super();
        this.username = username;
        this.title = title;
        this.mobile = mobile;
        this.nameCategory = nameCategory;
        this.description = description;
        this.cost = cost;
        this.location = location;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("username", username)
                .append("title", title)
                .append("mobile", mobile)
                .append("nameCategory", nameCategory)
                .append("description", description)
                .append("cost", cost)
                .append("location", location).toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getUpid() {
        return upid;
    }

    public void setUpid(Long upid) {
        this.upid = upid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getIdField() {
        return FIELD_ID;
    }
}
