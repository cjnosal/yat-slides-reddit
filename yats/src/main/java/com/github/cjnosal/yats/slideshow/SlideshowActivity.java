package com.github.cjnosal.yats.slideshow;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.github.cjnosal.yats.R;
import com.github.cjnosal.yats.YATSApplication;
import com.github.cjnosal.yats.modules.ApplicationComponent;
import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.services.RedditContentService;
import com.github.cjnosal.yats.slideshow.modules.DaggerSlideshowComponent;
import com.github.cjnosal.yats.slideshow.modules.SlideshowComponent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SlideshowActivity extends RxAppCompatActivity implements SlideshowContract.View {

    public static final String CURRENT_SLIDE = "current_slide";
    public static final String SLIDES = "slides";

    @Bind(R.id.slide_pager)
    ViewPager slidePager;

    @Inject
    RedditContentService redditService;

    @Inject
    AuthManager authManager;

    SlideshowContract.UserActionListener listener;
    SlideAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        ButterKnife.bind(this);

        ApplicationComponent applicationComponent = ((YATSApplication) getApplication()).getApplicationComponent();
        SlideshowComponent slideshowComponent = DaggerSlideshowComponent.builder().applicationComponent(applicationComponent).build();
        slideshowComponent.inject(this);

        adapter = new SlideAdapter();
        slideshowComponent.inject(adapter);
        slidePager.setAdapter(adapter);
        slidePager.setOffscreenPageLimit(4);

        slidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Timber.d("Display %s", adapter.getImages().get(position));
                if (position == (adapter.getCount() - 3)) {
                    listener.fetchUrls();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        listener = new SlideshowPresenter(redditService, authManager, this, savedInstanceState);
        if (savedInstanceState != null) {
            adapter.setImages(savedInstanceState.getStringArrayList(SLIDES));
            slidePager.setCurrentItem(savedInstanceState.getInt(CURRENT_SLIDE), false);
        } else {
            listener.fetchUrls();
        }
    }

    @Override
    public void displayImages(List<String> urls) {
        List<String> adapterImages = adapter.getImages();
        adapterImages.addAll(urls);
        adapter.setImages(adapterImages);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SLIDE, slidePager.getCurrentItem());
        outState.putStringArrayList(SLIDES, new ArrayList<>(adapter.getImages()));
        listener.onSaveInstanceState(outState);
    }
}
