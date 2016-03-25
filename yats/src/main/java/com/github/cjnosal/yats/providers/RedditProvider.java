package com.github.cjnosal.yats.providers;

import android.text.TextUtils;

import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.models.subreddit.Link;
import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;
import com.github.cjnosal.yats.network.services.RedditContentService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class RedditProvider {

    private static final String DEFAULT_SUB = "pics";
    private static final int NUM_IMAGES = 10;

    @Inject
    RedditContentService redditService;

    @Inject
    AuthManager authManager;

    boolean isLastPage = false;
    String lastImage = null;

    @Inject
    public RedditProvider() {
    }

    public Observable<List<String>> getImageUrls() {

        if (isLastPage) {
            return Observable.empty();
        } else if (authManager.isAuthenticated()) {
            return getUrlObservable();
        } else {
            return authManager.fetchAuthToken().flatMap(new Func1<AuthResponse, Observable<List<String>>>() {
                @Override
                public Observable<List<String>> call(AuthResponse authResponse) {
                    return getUrlObservable();
                }
            });
        }
    }

    private Observable<List<Link>> getLinkObservable() {
        return redditService.searchSubreddit(authManager.getOauthHeader(), DEFAULT_SUB, NUM_IMAGES, getTimeQuery(), lastImage)
                .flatMap(new Func1<SubredditSearchResponse, Observable<Link>>() {
                    @Override
                    public Observable<Link> call(SubredditSearchResponse subredditSearchResponse) {

                        String after = subredditSearchResponse.getListingData().getAfter();
                        if (TextUtils.isEmpty(after)) {
                            isLastPage = true;
                            lastImage = null;
                        } else {
                            isLastPage = false;
                            lastImage = after;
                        }
                        return Observable.from(subredditSearchResponse.getListingData().getLinks());
                    }
                })
                .toList();
    }

    private Observable<List<String>> getUrlObservable() {
        return getLinkObservable().flatMap(new Func1<List<Link>, Observable<String>>() {
            @Override
            public Observable<String> call(List<Link> links) {
                List<String> urls = new ArrayList<>(links.size());
                for (Link link : links) {
                    String url = getImageUrl(link);
                    if (!TextUtils.isEmpty(url)) {
                        urls.add(url);
                    }
                }
                return Observable.from(urls);
            }
        }).toList();
    }

    private String getImageUrl(Link link) {

        String linkUrl = link.getData().getUrl();

        String mediaUrl = null;
        if (link.getData().getMedia() != null) {
            mediaUrl = link.getData().getMedia().getOembed().getThumbnailUrl();
        }

        String previewUrl = null;
        if (link.getData().getPreview() != null) {
            previewUrl = link.getData().getPreview().getImages().get(0).getSource().getUrl();
        }

        String url = null;
        if (hasImageExtension(linkUrl)) {
            url = linkUrl;
            Timber.d("Using link url  %s", url);
        } else if (hasImageExtension(mediaUrl)) {
            url = mediaUrl;
            Timber.d("Using media url %s", url);
        } else if (hasImageExtension(previewUrl)) {
            url = previewUrl;
            Timber.d("Using preview url %s", url);
        } else {
            Timber.d("Skipping %s", linkUrl);
        }

        return url;
    }

    private boolean hasImageExtension(String url) {
        return !TextUtils.isEmpty(url) && (url.endsWith("png") || url.endsWith("jpg") || url.endsWith("jpeg"));
    }

    private String getTimeQuery() {
        Calendar calendar = Calendar.getInstance();

        // move forward to midnight
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // subtract a year
        calendar.add(Calendar.YEAR, -1);
        long end = calendar.getTimeInMillis() / 1000;

        // subtract a day
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long start = calendar.getTimeInMillis() / 1000;

        return String.format("timestamp:%1$s..%2$s", start, end);
    }
}
