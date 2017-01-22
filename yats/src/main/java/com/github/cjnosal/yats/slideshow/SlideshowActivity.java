package com.github.cjnosal.yats.slideshow;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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

    private static final String DEFAULT_SUB = "pics";
    private static final String CURRENT_SLIDE = "current_slide";
    private static final String SLIDES = "slides";

    @Bind(R.id.slide_pager)
    ViewPager slidePager;

    @Bind(R.id.slide_progress_bar)
    ContentLoadingProgressBar progressBar;

    @Bind(R.id.root)
    View rootView;

    @Bind(R.id.app_bar)
    AppBarLayout appBarLayout;

    @Bind(R.id.subreddit_edit_text)
    EditText subredditEditText;

    @Inject
    SlideshowContract.Presenter presenter;

    @Inject
    SlideAdapter adapter;

    int slidePosition = 0;
    float slideOffset = 0;

    String subreddit = DEFAULT_SUB;

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
                    loadImages(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    appBarLayout.setVisibility(View.VISIBLE);
                    subredditEditText.requestFocus();
                    subredditEditText.setSelection(0, subredditEditText.getText().length());
                    leaveImmersiveMode();
                } else {
                    appBarLayout.setVisibility(View.GONE);
                    enterImmersiveMode();
                }
            }
        });

        subredditEditText.setText(DEFAULT_SUB);
        subredditEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                subreddit = v.getText().toString();
                loadImages(true);
                enterImmersiveMode();
                return false;
            }
        });

        subredditEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    subredditEditText.setText("");
                }
            }
        });

        slidePager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    enterImmersiveMode();
                }
                return false;
            }
        });

        presenter.init(this);
        if (savedInstanceState != null) {
            adapter.setImages((List<Slide>)savedInstanceState.getSerializable(SLIDES));
            slidePager.setCurrentItem(savedInstanceState.getInt(CURRENT_SLIDE), false);
        } else {
            loadImages(true);
        }
    }

    private void loadImages(boolean reset) {
        presenter.findImages(subreddit, reset);
        progressBar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enterImmersiveMode();
    }

    private void enterImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    private void leaveImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_VISIBLE);
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
        progressBar.getIndeterminateDrawable().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
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
        adapter.setImages(slides);
        slidePager.setCurrentItem(0, false);
        progressBar.hide();
    }

    @Override
    public void addImages(List<Slide> slides) {
        List<Slide> adapterImages = adapter.getSlides();
        adapterImages.addAll(slides);
        adapter.setImages(adapterImages);
        progressBar.hide();
    }

    @Override
    public void loadFailed() {
        progressBar.hide();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SLIDE, slidePager.getCurrentItem());
        outState.putSerializable(SLIDES, new ArrayList<>(adapter.getSlides()));
    }
}
