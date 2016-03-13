package com.github.cjnosal.yats;

import android.app.Application;

import com.github.cjnosal.yats.modules.ApplicationComponent;
import com.github.cjnosal.yats.modules.ApplicationModule;
import com.github.cjnosal.yats.modules.DaggerApplicationComponent;

import timber.log.Timber;

public class YATSApplication extends Application {

    static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        ApplicationModule applicationModule = new ApplicationModule(getApplicationContext());
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(applicationModule).build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
