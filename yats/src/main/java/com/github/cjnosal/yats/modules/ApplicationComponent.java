package com.github.cjnosal.yats.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.cjnosal.yats.config.UserSettings;
import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.services.RedditAuthService;
import com.github.cjnosal.yats.network.services.RedditContentService;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {
    Context applicationContext();
    SharedPreferences sharedPreferences();
    UserSettings userSettings();

    RedditContentService redditContentService();
    RedditAuthService redditAuthService();
    AuthManager authManager();
    Picasso picasso();
}
