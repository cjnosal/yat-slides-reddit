package com.github.cjnosal.yats.network.services;

import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;

import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RedditService {

    @POST("api/v1/access_token")
    @Multipart
    Observable<AuthResponse> oauth2(@Part(value = "grant_type") RequestBody grant, @Part(value = "device_id") RequestBody deviceId, @Header("Authorization") String authHeader);

    // TODO switch to authenticated api
    @GET("/r/{subreddit}/search.json?syntax=cloudsearch&sort=top&restrict_sr=on")
    Observable<SubredditSearchResponse> searchSubReddit(@Path("subreddit") String subreddit, @Query("limit") int limit, @Query("q") String search);
}
