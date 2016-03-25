package com.github.cjnosal.yats.slideshow.modules;

import com.github.cjnosal.yats.annotations.ActivityScope;
import com.github.cjnosal.yats.providers.RedditProvider;
import com.github.cjnosal.yats.slideshow.SlideAdapter;
import com.github.cjnosal.yats.slideshow.SlideshowContract;
import com.github.cjnosal.yats.slideshow.SlideshowPresenter;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class SlideshowModule {
    @Provides
    @ActivityScope
    SlideshowContract.Presenter presenter(RedditProvider provider) {
        return new SlideshowPresenter(provider);
    }

    @Provides
    @ActivityScope
    SlideAdapter adapter(Picasso picasso) {
        return new SlideAdapter(picasso);
    }
}
