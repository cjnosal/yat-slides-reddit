package com.github.cjnosal.yats.providers;

import com.github.cjnosal.yats.config.UserSettings;
import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;
import com.github.cjnosal.yats.network.services.RedditContentService;

import java.util.Calendar;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RedditProvider {

    private RedditContentService redditService;
    private AuthManager authManager;
    private UserSettings userSettings;

    @Inject
    public RedditProvider(RedditContentService redditService, AuthManager authManager, UserSettings userSettings) {
        this.redditService = redditService;
        this.authManager = authManager;
        this.userSettings = userSettings;
    }

    public Observable<SubredditSearchResponse> searchSubreddit(final String subreddit, final int numImages, final Calendar startTime, final Calendar endTime, final String lastImage) {
        if (authManager.isAuthenticated()) {
            return search(subreddit, numImages, startTime, endTime, lastImage);
        } else {
            return authManager.fetchAuthToken().flatMap(new Func1<AuthResponse, Observable<SubredditSearchResponse>>() {
                @Override
                public Observable<SubredditSearchResponse> call(AuthResponse authResponse) {
                    return search(subreddit, numImages, startTime, endTime, lastImage);
                }
            });
        }
    }

    private Observable<SubredditSearchResponse> search(final String subreddit, final int numImages, final Calendar startTime, final Calendar endTime, final String lastImage) {
        return redditService.searchSubreddit(authManager.getOauthHeader(), subreddit, numImages, getQuery(startTime, endTime), lastImage)
                .subscribeOn(Schedulers.io());
    }

    private String getQuery(final Calendar startTime, final Calendar endTime) {
        String query = getNsfwQuery();
        if (startTime != null && endTime != null) {
            query += ' ' + getTimeQuery(startTime, endTime);
        }
        return query;
    }

    private String getNsfwQuery() {
        return "nsfw:" + (userSettings.includeNsfw() ? '1' : '0');
    }

    private String getTimeQuery(Calendar startTime, Calendar endTime) {
        long endTimeSeconds = endTime.getTimeInMillis() / 1000;
        long startTimeSeconds = startTime.getTimeInMillis() / 1000;

        return String.format("timestamp:%1$s..%2$s", startTimeSeconds, endTimeSeconds);
    }
}
