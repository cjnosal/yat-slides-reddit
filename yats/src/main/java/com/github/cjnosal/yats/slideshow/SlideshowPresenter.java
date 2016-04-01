package com.github.cjnosal.yats.slideshow;

import com.github.cjnosal.yats.providers.RedditProvider;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
        redditProvider.getImageUrls()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> urls) {
                        view.displayImages(urls);
                    }
                });
    }
}
