package com.shifu.user.shifu_5_newavito.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


import java.util.Random;
import java.util.UUID;

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
    private Integer mobile;

    @SerializedName("location")
    @Expose
    private String location;


    public Author() {}

    public Author(String username, String password){
        this.username = username;
        this.password = password;

        //TODO myTest - пока эти поля не убраны на беке
        this.mobile = new Random().nextInt();
        this.location = UUID.randomUUID().toString().substring(0, 30);
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
