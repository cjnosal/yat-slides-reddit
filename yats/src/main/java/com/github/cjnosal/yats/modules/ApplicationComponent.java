package com.github.cjnosal.yats.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.services.RedditService;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent extends ApplicationInjector {
    Context applicationContext();
    RedditService redditService();
    SharedPreferences sharedPreferences();
    AuthManager authManager();
    Picasso picasso();
}
