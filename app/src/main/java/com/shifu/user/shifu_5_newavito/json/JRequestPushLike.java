package com.shifu.user.shifu_5_newavito.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JRequestPushLike {

    @SerializedName("id_article")
    @Expose
    private Long id_article;

    @SerializedName("username")
    @Expose
    private String username;

    public JRequestPushLike(Long id_article, String username) {
        this.id_article = id_article;
        this.username = username;
    }
}
