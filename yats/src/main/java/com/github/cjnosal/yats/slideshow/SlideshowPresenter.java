package com.github.cjnosal.yats.slideshow;

import android.text.TextUtils;

import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.models.subreddit.Child;
import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;
import com.github.cjnosal.yats.network.services.RedditService;
import com.github.cjnosal.yats.rxjava.RxUtils;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class SlideshowPresenter implements SlideshowContract.UserActionListener {

    private static final String DEFAULT_SUB = "pics";
    private static final int NUM_IMAGES = 10;

    RedditService redditService;
    AuthManager authManager;
    SlideshowContract.View view;

    public SlideshowPresenter(RedditService redditService, AuthManager authManager, SlideshowContract.View view) {
        this.redditService = redditService;
        this.authManager = authManager;
        this.view = view;
    }

    public void fetchImages() {

        String query = getTimeQuery();

        Observable<List<String>> observable = redditService.searchSubReddit(DEFAULT_SUB, NUM_IMAGES, query)
                .map(new Func1<SubredditSearchResponse, List<Child>>() {
                    @Override
                    public List<Child> call(SubredditSearchResponse subredditSearchResponse) {
                        return subredditSearchResponse.data.children;
                    }
                })
                .map(new Func1<List<Child>, List<String>>() {
                    @Override
                    public List<String> call(List<Child> children) {
                        // TODO flatmap/filter observables
                        List<String> urls = new ArrayList<String>(25);
                        for (Child child : children) {
                            String url = child.data.url;
                            if (!TextUtils.isEmpty(url)) {

                                // TODO more extensions, check data.media.url
                                boolean directLink = url.endsWith("png") || url.endsWith("jpg");
                                if (directLink) {
                                    urls.add(url);
                                }
                            }
                        }
                        return urls;
                    }
                });

        RxUtils.subscribeIO(observable, new Subscriber<List<String>>() {
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

                for (String url : urls) {
                    Timber.d(url);
                }
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
