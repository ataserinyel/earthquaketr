package com.example.deprem.network;

import com.example.deprem.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("deprem/kandilli/live")
    Call<ApiResponse> getLatestEarthquakes();
}
