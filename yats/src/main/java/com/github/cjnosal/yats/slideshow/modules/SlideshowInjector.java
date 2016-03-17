package com.github.cjnosal.yats.slideshow.modules;

import com.github.cjnosal.yats.slideshow.SlideAdapter;
import com.github.cjnosal.yats.slideshow.SlideshowActivity;

public interface SlideshowInjector {
    // unscoped injections
    void inject(SlideshowActivity slideshowActivity);
    void inject(SlideAdapter adapter);
}
