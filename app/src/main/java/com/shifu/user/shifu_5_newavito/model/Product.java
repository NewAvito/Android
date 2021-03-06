package com.shifu.user.shifu_5_newavito.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import io.realm.RealmObject;

public class Product extends RealmObject implements MyRealms{

    private final static String FIELD_ID = "upid";

    //PrimaryKey
    @SerializedName("id_article")
    @Expose
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

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("cost")
    @Expose
    private Long cost;

    @SerializedName("numphone")
    @Expose
    private String mobile;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("like")
    @Expose
    private Long likes;

    public Product() {
    }

    public Product(Long upid, String username, String title, String mobile, String category, String description, Long cost, String location, String url) {
        super();
        this.upid = upid;
        this.date = new Date();
        this.username = username;
        this.title = title;
        this.mobile = mobile;
        this.category = category;
        this.description = description;
        this.cost = cost;
        this.location = location;
        this.likes = 0L;
        this.url = url;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("username", username)
                .append("title", title)
                .append("mobile", mobile)
                .append("category", category)
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public static String getNetIdField() {
        return FIELD_ID;
    }
}
