package com.shifu.user.shifu_5_newavito.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Author extends RealmObject implements MyRealms{

    private final static String FIELD_ID = "username";

    @SerializedName(FIELD_ID)
    @Expose
    @PrimaryKey
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("location")
    @Expose
    private String location;


    public Author() {}

    public Author(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Author(String username, String password, String mobile, String location){
        this.username = username;
        this.password = password;
        this.mobile = mobile;
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this)
                .append("username", username)
                .append("pass", password)
                .append("mobile", mobile)
                .append("location", location)
                .toString();
    }

    @Override
    public String getIdField() {
        return FIELD_ID;
    }

}
