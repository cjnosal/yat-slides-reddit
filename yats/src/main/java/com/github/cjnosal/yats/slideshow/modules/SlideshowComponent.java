package com.github.cjnosal.yats.slideshow.modules;

import com.github.cjnosal.yats.annotations.ActivityScope;
import com.github.cjnosal.yats.modules.ApplicationComponent;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = SlideshowModule.class)
public interface SlideshowComponent extends SlideshowInjector {
    // TODO slideshow dependencies
}
