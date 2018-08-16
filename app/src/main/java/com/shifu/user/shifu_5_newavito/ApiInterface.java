package com.shifu.user.shifu_5_newavito;


import com.shifu.user.shifu_5_newavito.json.JsonAuthorResponse;
import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Empty;
import com.shifu.user.shifu_5_newavito.model.Product;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    String type = "mobile";

    @POST("/{type}")
    Flowable<Response<JsonAuthorResponse>> login(@Path("type") String requestType, @Body Author author);

    @GET("/article")
    Single<Response<List<Product>>> getProducts(@Query("page") long page);

    @POST("/new_article")
    Single<Response<Empty>> pushProduct(@Body Product product);

    @Multipart
    @POST("/new_image")
    Call<Response<Empty>> pushImage(@Part MultipartBody.Part file);



}
