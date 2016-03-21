package com.github.cjnosal.yats.slideshow.modules;

import com.github.cjnosal.yats.annotations.ActivityScope;
import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.services.RedditContentService;
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
    SlideshowContract.UserActionListener presenter(RedditContentService redditService, AuthManager authManager) {
        return new SlideshowPresenter(redditService, authManager);
    }

    @Provides
    @ActivityScope
    SlideAdapter adapter(Picasso picasso) {
        return new SlideAdapter(picasso);
    }
}
