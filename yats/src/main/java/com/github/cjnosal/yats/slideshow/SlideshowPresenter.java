package com.github.cjnosal.yats.slideshow;

import com.github.cjnosal.yats.providers.RedditProvider;
import com.github.cjnosal.yats.rxjava.RxUtils;

import java.util.List;

import rx.Subscriber;
import timber.log.Timber;

public class SlideshowPresenter implements SlideshowContract.Presenter {

    RedditProvider redditProvider;

    SlideshowContract.View view;

    public SlideshowPresenter(RedditProvider redditProvider) {
        this.redditProvider = redditProvider;
    }

    public void init(SlideshowContract.View view) {
        this.view = view;
    }

    public void findImages() {
        RxUtils.subscribeIO(redditProvider.getImageUrls(), new Subscriber<List<String>>() {
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
}
