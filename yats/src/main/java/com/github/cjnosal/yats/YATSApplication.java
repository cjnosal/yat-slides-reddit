package com.github.cjnosal.yats;

import android.app.Application;
import android.os.StrictMode;

import com.github.cjnosal.yats.modules.ApplicationComponent;
import com.github.cjnosal.yats.modules.ApplicationModule;
import com.github.cjnosal.yats.modules.DaggerApplicationComponent;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class YATSApplication extends Application {

    static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Debugging
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
            LeakCanary.install(this);
        }
        // Injection
        ApplicationModule applicationModule = new ApplicationModule(getApplicationContext());
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(applicationModule).build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
