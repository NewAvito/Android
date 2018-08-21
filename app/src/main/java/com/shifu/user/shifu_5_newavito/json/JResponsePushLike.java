package com.shifu.user.shifu_5_newavito.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JResponsePushLike {


    @SerializedName("answer")
    @Expose
    private String answer;

    public String getAnswer() {
        return answer;
    }
}
