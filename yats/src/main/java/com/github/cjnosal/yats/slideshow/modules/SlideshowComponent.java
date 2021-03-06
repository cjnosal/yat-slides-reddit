package com.github.cjnosal.yats.slideshow.modules;

import com.github.cjnosal.yats.annotations.ActivityScope;
import com.github.cjnosal.yats.modules.ApplicationComponent;
import com.github.cjnosal.yats.slideshow.SlideshowActivity;
import com.github.cjnosal.yats.slideshow.SlideshowContract;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = SlideshowModule.class)
public interface SlideshowComponent {
    SlideshowContract.Presenter presenter();

    void inject(SlideshowActivity slideshowActivity);
}
