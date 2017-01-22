package com.github.cjnosal.yats.slideshow.modules;

import com.github.cjnosal.yats.annotations.ActivityScope;
import com.github.cjnosal.yats.slideshow.SlideshowContract;
import com.github.cjnosal.yats.slideshow.SlideshowPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SlideshowModule {
    @Provides
    @ActivityScope
    SlideshowContract.Presenter presenter(SlideshowPresenter presenter) {
        return presenter;
    }
}
