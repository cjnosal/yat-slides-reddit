package com.github.cjnosal.yats.slideshow;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;

import com.github.cjnosal.yats.R;
import com.github.cjnosal.yats.YATSApplication;
import com.github.cjnosal.yats.modules.ApplicationComponent;
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
    SlideshowContract.Presenter presenter;

    @Inject
    SlideAdapter adapter;

    int slidePosition = 0;
    float slideOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        ButterKnife.bind(this);

        ApplicationComponent applicationComponent = ((YATSApplication) getApplication()).getApplicationComponent();
        SlideshowComponent slideshowComponent = DaggerSlideshowComponent.builder().applicationComponent(applicationComponent).build();
        slideshowComponent.inject(this);

        adapter.setListener(new SlideAdapter.Listener() {
            @Override
            public void onImageLoaded() {
                setBackgroundColor();
            }

            @Override
            public void onImageFailed() {
            }
        });
        slidePager.setAdapter(adapter);
        slidePager.setOffscreenPageLimit(4);

        slidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                slidePosition = position;
                slideOffset = positionOffset;
                setBackgroundColor();
            }

            @Override
            public void onPageSelected(int position) {
                Timber.d("Display %s at position %d", adapter.getImages().get(position), position);

                slidePosition = position;
                slideOffset = 0;
                setBackgroundColor();

                if (!presenter.isLastPage() && position == (adapter.getCount() - 3)) {
                    presenter.fetchUrls();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        presenter.init(this, savedInstanceState);
        if (savedInstanceState != null) {
            adapter.setImages(savedInstanceState.getStringArrayList(SLIDES));
            slidePager.setCurrentItem(savedInstanceState.getInt(CURRENT_SLIDE), false);
        } else {
            presenter.fetchUrls();
        }
    }

    private void setBackgroundColor() {

        Palette left = adapter.getPalette(slidePosition);
        Palette right = adapter.getPalette(slidePosition + 1); // null for last slide

        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, a, true);

        @ColorInt int backgroundColor = a.data;
        if (left != null && right != null) {
            @ColorInt int leftColor = left.getMutedColor(backgroundColor);
            @ColorInt int rightColor = right.getMutedColor(backgroundColor);
            backgroundColor = ColorUtils.blendARGB(leftColor, rightColor, slideOffset);
        } else if (left != null) {
            backgroundColor = left.getMutedColor(backgroundColor);
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
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
        presenter.onSaveInstanceState(outState);
    }
}
