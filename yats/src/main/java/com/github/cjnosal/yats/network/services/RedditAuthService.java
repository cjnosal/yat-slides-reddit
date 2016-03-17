package com.github.cjnosal.yats.network.services;

import com.github.cjnosal.yats.network.models.AuthResponse;

import okhttp3.RequestBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface RedditAuthService {

    @POST("api/v1/access_token")
    @Multipart
    Observable<AuthResponse> oauth2(@Part(value = "grant_type") RequestBody grant, @Part(value = "device_id") RequestBody deviceId, @Header("Authorization") String authHeader);

}
