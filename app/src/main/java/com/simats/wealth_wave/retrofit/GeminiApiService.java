package com.simats.wealth_wave.retrofit;

import com.simats.wealth_wave.models.GeminiRequest;
import com.simats.wealth_wave.responses.GeminiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface GeminiApiService {
    @POST
    Call<GeminiResponse> generateContent(@Url String url, @Body GeminiRequest request);
}
