package com.shifu.user.shifu_5_newavito;

import com.shifu.user.shifu_5_newavito.json.JRequestPushLike;
import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.json.JResponsePushProduct;
import com.shifu.user.shifu_5_newavito.model.Category;
import com.shifu.user.shifu_5_newavito.model.Product;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    String format = "json";
    String contentType = "application/json";

    @POST("./{type}/?format=json")
    Flowable<Response<Author>> login(
            @Path("type") String requestType,
            @Body Author author);

    @GET("./category/?format=json")
    Flowable<Response<List<Category>>> getCategories();

    @GET("./mob_article/")
    Flowable<Response<List<Product>>> getProducts(
            @QueryMap Map<String, String> options,
            @HeaderMap Map<String, String> headers);


    @POST("./article/?format=json")
    Flowable<Response<JResponsePushProduct>> pushProduct(
            @Header("Content-Type") String contentType,
            @Body Product product);

//    @POST("./like/?format=json")
//    Flowable<Response<JRequestPushLike>> pushLike(
//            @Body JRequestPushLike likeRequest
//    );


}
