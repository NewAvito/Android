package com.shifu.user.shifu_5_newavito.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.realm.RealmObject;

public class Category extends RealmObject{

    @SerializedName("category")
    @Expose
    private String category;

    @Override
    public String toString(){
        return new ToStringBuilder(this)
                .append("category", category)
                .toString();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
