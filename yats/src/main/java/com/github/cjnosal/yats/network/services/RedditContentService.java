package com.github.cjnosal.yats.network.services;

import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RedditContentService {

    @GET("/r/{subreddit}/search.json?syntax=cloudsearch&sort=top&restrict_sr=on&show=all")
    Observable<SubredditSearchResponse> searchSubreddit(
            @Header("Authorization") String authHeader,
            @Path("subreddit") String subreddit,
            @Query("limit") int limit,
            @Query("q") String search,
            @Query("after") String after);

}
