package com.github.cjnosal.yats.slideshow;

import android.text.TextUtils;

import com.github.cjnosal.yats.network.LinkUtil;
import com.github.cjnosal.yats.network.models.subreddit.Link;
import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;
import com.github.cjnosal.yats.providers.RedditProvider;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SlideshowPresenter implements SlideshowContract.Presenter {

    private static final String DEFAULT_SUB = "pics";
    private static final int NUM_IMAGES = 10;

    private RedditProvider redditProvider;
    private LinkUtil linkUtil;

    private SlideshowContract.View view;

    private String lastImage;
    private Calendar startOfDay;
    private Calendar endOfDay;

    @Inject
    public SlideshowPresenter(RedditProvider redditProvider, LinkUtil linkUtil) {
        this.redditProvider = redditProvider;
        this.linkUtil = linkUtil;
        initDates();
    }

    public void init(SlideshowContract.View view) {
        this.view = view;
    }

    public void findImages() {
        redditProvider.searchSubreddit(DEFAULT_SUB, NUM_IMAGES, startOfDay, endOfDay, lastImage)
                .observeOn(Schedulers.computation())
                .doOnNext(new Action1<SubredditSearchResponse>() {
                    @Override
                    public void call(SubredditSearchResponse subredditSearchResponse) {
                        String after = subredditSearchResponse.getListingData().getAfter();
                        if (TextUtils.isEmpty(after)) {
                            moveOneDayBack();
                            lastImage = null;
                        } else {
                            lastImage = after;
                        }
                    }
                })
                .flatMap(new Func1<SubredditSearchResponse, Observable<Link>>() {
                    @Override
                    public Observable<Link> call(SubredditSearchResponse subredditSearchResponse) {
                        List<Link> links = subredditSearchResponse.getListingData().getLinks();
                        Timber.d("%d Reddit posts found", links.size());
                        return Observable.from(links);
                    }
                })
                .map(new Func1<Link, Slide>() {
                    @Override
                    public Slide call(Link link) {
                        String url = linkUtil.getImageUrl(link);
                        return new Slide(url, link.getData().getTitle(), link.getData().getSelftext());
                    }
                })
                .filter(new Func1<Slide, Boolean>() {
                    @Override
                    public Boolean call(Slide link) {
                        return link.getImageUrl() != null;
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Slide>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Failed to get slides");
                    }

                    @Override
                    public void onNext(List<Slide> slides) {
                        Timber.d("%d image urls found", slides.size());
                        view.displayImages(slides);
                    }
                });
    }

    private void initDates() {
        endOfDay = Calendar.getInstance();

        // move forward to midnight
        endOfDay.add(Calendar.DAY_OF_YEAR, 1);
        endOfDay.set(Calendar.HOUR_OF_DAY, 0);
        endOfDay.set(Calendar.MINUTE, 0);
        endOfDay.set(Calendar.SECOND, 0);

        // subtract a year
        endOfDay.add(Calendar.YEAR, -1);

        startOfDay = Calendar.getInstance();
        startOfDay.setTime(endOfDay.getTime());

        // subtract a day
        startOfDay.add(Calendar.DAY_OF_YEAR, -1);
    }

    private void moveOneDayBack() {
        startOfDay.add(Calendar.DAY_OF_YEAR, -1);
        endOfDay.add(Calendar.DAY_OF_YEAR, -1);
    }


}
