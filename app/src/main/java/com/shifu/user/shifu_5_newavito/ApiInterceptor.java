package com.shifu.user.shifu_5_newavito;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;

public class ApiInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) {
        try {
            Request request = chain.request();
            Response response = chain.proceed(request);
            Log.d("Request: ", request.url().toString());
            Log.d("Request headers: ", request.headers().toString());

            // Обработка путого запроса (самостоятельно retrofit обрабатывает некорректно - нужен json)
            if (response.body() != null) {
                BufferedSource source = response.body().source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Log.d("Response", "size: " + buffer.size() + "-byte body");
                Log.d("Response", "body: " + response.body().string());
                Log.d("Response", "code: " + response.code());

//                String content;
//                MediaType contentType = MediaType.parse("application/json;");
//                ResponseBody body;
//                if (buffer.size() == 0) {
//                    System.out.println("Response body is empty");
//                    content = "[{}]";
//                    body = ResponseBody.create(contentType, content);
//                } else {
//                    body = ResponseBody.create(contentType, response.body().bytes());
//                }
//                response = response.newBuilder().body(body).build();

            }
            return response;
        } catch (Exception e) {
            onRestError(e);
            ResponseBody body = ResponseBody.create(MediaType.parse("application/json;"), "[{}]");
            return new Response.Builder()
                    .code(520).message("Нет корректного ответа от сервера")
                    .body(body)
                    .protocol(Protocol.HTTP_1_0)
                    .request(chain.request())
                    .build();
        }
    }

    private static void onRestError(Throwable e) {
        String TAG = "onRestError";
        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException)e).response().errorBody();
            Log.d(TAG, getErrorMessage(responseBody));
        } else if (e instanceof SocketTimeoutException) {
            Log.d(TAG, "SocketTimeoutException");
        } else if (e instanceof IOException) {
            Log.d(TAG, "IOException");
            e.printStackTrace();
        } else {
            Log.d(TAG, e.getMessage());
        }
    }

    private static String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
