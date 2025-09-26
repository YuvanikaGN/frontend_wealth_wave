package com.simats.wealth_wave.retrofit;

import static com.simats.wealth_wave.retrofit.ApiClient.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simats.wealth_wave.models.UpdatePlanRequest;
import com.simats.wealth_wave.models.UserManager;
import com.simats.wealth_wave.responses.UpdatePlanResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static final String BASE_URL = "http://10.24.93.232/app_database/";
    private static RetrofitClient instance = null;
    private ApiService api;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiService.class);


    }

    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApi() {
        return api;
    }

    public static Retrofit getClient(Context context, String baseUrl) {
        if (retrofit == null) {
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

            // Interceptor to add Authorization header from UserManager
            httpBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder rb = original.newBuilder();
                String token = UserManager.getInstance(context).getToken();
                if (token != null && !token.isEmpty()) {
                    rb.header("Authorization", "Bearer " + token);
                }
                Request req = rb.build();
                return chain.proceed(req);
            });

            OkHttpClient client = httpBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}


