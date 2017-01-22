package com.github.cjnosal.yats.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.services.RedditAuthService;
import com.github.cjnosal.yats.network.services.RedditContentService;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {
    // ApplicationModule
    Context applicationContext();
    SharedPreferences sharedPreferences();

    // NetworkModule
    RedditContentService redditContentService();
    RedditAuthService redditAuthService();
    Picasso picasso();

    // SlideshowModule workaround for scoping difference?
    AuthManager authManager();
}
