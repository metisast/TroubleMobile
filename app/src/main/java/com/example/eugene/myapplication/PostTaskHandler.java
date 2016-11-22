package com.example.eugene.myapplication;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eugene on 11/21/16.
 */

public class PostTaskHandler {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType STR = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String deleteTask(String url, String params) throws IOException {
        RequestBody body = RequestBody.create(STR, params);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    String deleteTask(String task_id){
        return "{task_id: 206}";
    }
}
