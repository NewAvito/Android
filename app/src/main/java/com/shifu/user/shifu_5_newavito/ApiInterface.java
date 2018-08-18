package com.shifu.user.shifu_5_newavito;


import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Category;
import com.shifu.user.shifu_5_newavito.model.Empty;
import com.shifu.user.shifu_5_newavito.model.Product;

import java.util.List;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    String format = "json";
    String contentType = "application/json";


    @POST("./{type}/")
    Flowable<Response<Author>> login(
            @Path("type") String requestType,
            @Query("format") String format,
            @Body Author author);

    @GET("./category/")
    Flowable<Response<List<Category>>> getCategories(@Query("format") String format);

    @GET("./article/")
    Flowable<Response<List<Product>>> getProducts(@Query("format") String format, @Query("page") long page);

    @GET("./article")
    Flowable<Response<List<Product>>> getProducts(@Query("format") String format);

    @POST("./article")
    Flowable<Response<Empty>> pushProduct(@Header("Content-Type") String contentType, @Body Product product);

    @Multipart
    @POST("/new_image")
    Call<Response<Empty>> pushImage(@Part MultipartBody.Part file);



}
