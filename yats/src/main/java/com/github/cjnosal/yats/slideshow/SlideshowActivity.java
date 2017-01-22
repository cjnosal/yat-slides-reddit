package com.github.cjnosal.yats.slideshow;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;
import android.view.View;

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
        slidePager.setOffscreenPageLimit(2);

        slidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                slidePosition = position;
                slideOffset = positionOffset;
                setBackgroundColor();
            }

            @Override
            public void onPageSelected(int position) {
                Timber.d("Display %s at position %d", adapter.getSlides().get(position).getImageUrl(), position);

                slidePosition = position;
                slideOffset = 0;
                setBackgroundColor();

                if (position == (adapter.getCount() - 3)) {
                    presenter.findImages();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        presenter.init(this);
        if (savedInstanceState != null) {
            adapter.setImages((List<Slide>)savedInstanceState.getSerializable(SLIDES));
            slidePager.setCurrentItem(savedInstanceState.getInt(CURRENT_SLIDE), false);
        } else {
            presenter.findImages();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setBackgroundColor() {

        Palette left = adapter.getPalette(slidePosition);
        Palette right = adapter.getPalette(slidePosition + 1); // null for last slide

        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, a, true);

        @ColorInt int backgroundColor = getColorFromPalettes(left, right, a.data);

        if (backgroundColor == a.data) {
            Timber.w("Failed to generate swatch for index " + slidePosition);
        }
        slidePager.setBackgroundColor(backgroundColor);
    }

    private @ColorInt int getColorFromPalettes(Palette left, Palette right, @ColorInt int defaultColor) {
        @ColorInt int leftColor = getColorFromPalette(left, defaultColor);
        @ColorInt int rightColor = getColorFromPalette(right, defaultColor);
        return ColorUtils.blendARGB(leftColor, rightColor, slideOffset);
    }

    private @ColorInt int getColorFromPalette(Palette palette, @ColorInt int defaultColor) {
        if (palette == null) {
            return defaultColor;
        }
        Palette.Swatch swatch = palette.getMutedSwatch();
        if (swatch == null) {
            swatch = palette.getVibrantSwatch();
        }
        if (swatch == null) {
            return defaultColor;
        }
        return swatch.getRgb();
    }

    @Override
    public void displayImages(List<Slide> slides) {
        List<Slide> adapterImages = adapter.getSlides();
        adapterImages.addAll(slides);
        adapter.setImages(adapterImages);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SLIDE, slidePager.getCurrentItem());
        outState.putSerializable(SLIDES, new ArrayList<>(adapter.getSlides()));
    }
}
