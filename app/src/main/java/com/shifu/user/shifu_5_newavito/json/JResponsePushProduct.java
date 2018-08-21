package com.shifu.user.shifu_5_newavito.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JResponsePushProduct {

    @SerializedName("id_article")
    @Expose
    private Long id_article;

    @SerializedName("answer")
    @Expose
    private String answer;

    public Long getId_article() {
        return id_article;
    }

    public String getAnswer() {
        return answer;
    }
}
