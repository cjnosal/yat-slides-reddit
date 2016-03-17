package com.github.cjnosal.yats.slideshow;

import android.os.Bundle;
import android.text.TextUtils;

import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.models.subreddit.Child;
import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;
import com.github.cjnosal.yats.network.services.RedditContentService;
import com.github.cjnosal.yats.rxjava.RxUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class SlideshowPresenter implements SlideshowContract.UserActionListener {

    private static final String DEFAULT_SUB = "pics";
    private static final int NUM_IMAGES = 10;
    private static final String LAST_IMAGE = "last_image";

    RedditContentService redditService;
    AuthManager authManager;
    SlideshowContract.View view;

    String lastImage;

    public SlideshowPresenter(RedditContentService redditService, AuthManager authManager, SlideshowContract.View view, Bundle bundle) {
        this.redditService = redditService;
        this.authManager = authManager;
        this.view = view;

        if (bundle != null) {
            lastImage = bundle.getString(LAST_IMAGE);
        }
    }

    public void fetchUrls() {
        if (authManager.isAuthenticated()) {
            searchImages();
        } else {
            Observable<AuthResponse> authResponseObservable = authManager.fetchAuthToken();
            RxUtils.subscribeIO(authResponseObservable, new Subscriber<AuthResponse>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(AuthResponse authResponse) {
                    searchImages();
                }
            });
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LAST_IMAGE, lastImage);
    }

    private void searchImages() {
        Observable<List<String>> urlsObservable = redditService.searchSubreddit(authManager.getOauthHeader(), DEFAULT_SUB, NUM_IMAGES, getTimeQuery(), lastImage)
                .flatMap(new Func1<SubredditSearchResponse, Observable<String>>() {
                    @Override
                    public Observable<String> call(SubredditSearchResponse subredditSearchResponse) {
                        List<String> urls = new ArrayList<String>(subredditSearchResponse.data.children.size());
                        for (Child child : subredditSearchResponse.data.children) {
                            String url = child.data.url;
                            if (!TextUtils.isEmpty(url)) {
                                boolean directLink = url.endsWith("png") || url.endsWith("jpg");
                                if (directLink) {
                                    Timber.d(url);
                                    urls.add(url);
                                } else {
                                    Timber.d("Skipping %s", url);
                                }
                            }
                        }
                        lastImage = subredditSearchResponse.data.after;
                        return Observable.from(urls);
                    }
                })
                .toList();

        RxUtils.subscribeIO(urlsObservable, new Subscriber<List<String>>() {
            @Override
            public void onCompleted() {
                Timber.d("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "onError");
            }

            @Override
            public void onNext(List<String> urls) {
                Timber.d("onNext");
                view.displayImages(urls);
            }
        });
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
