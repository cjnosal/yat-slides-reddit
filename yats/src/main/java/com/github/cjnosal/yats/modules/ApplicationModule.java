package com.github.cjnosal.yats.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.cjnosal.yats.providers.RedditProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private static final String PREFERENCES_FILE = "prefs";

    private Context applicationContext;

    public ApplicationModule(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Provides
    @Singleton
    Context providesApplicationContext() {
        return applicationContext;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences() {
        return applicationContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    RedditProvider providesRedditProvider() {
        return new RedditProvider();
    }


}
