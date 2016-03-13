package com.github.cjnosal.yats.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.github.cjnosal.yats.BuildConfig;
import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.services.RedditService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

@Module
public class ApplicationModule {

    private static final int NETWORK_TIMEOUT = 15; // seconds
    private static final String OKHTTP = "OkHttp";
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String BASE_URL = "https://www.reddit.com";
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
    AuthManager providesAuthManager(RedditService redditService, SharedPreferences sharedPreferences) {
        return new AuthManager(redditService, sharedPreferences);
    }

    @Singleton
    @Provides
    @Named("Logger")
    public Interceptor providesLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.tag(OKHTTP).d(message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Singleton
    @Provides
    @Named("UserAgent")
    public Interceptor providesUserAgentInterceptor() {
        return new Interceptor() {
            private final String userAgent = "android:" + BuildConfig.APPLICATION_ID + ":" + BuildConfig.VERSION_NAME + " (" + BuildConfig.ATTRIBUTION + ")";
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request().newBuilder().addHeader("User-Agent", userAgent).build();
                return chain.proceed(request);
            }
        };
    }

    @Singleton
    @Provides
    public Cache providesCache() {
        File cacheDir = applicationContext.getCacheDir();
        cacheDir = new File(cacheDir, OKHTTP);
        //noinspection ResultOfMethodCallIgnored
        cacheDir.mkdirs();
        return new Cache(cacheDir, CACHE_SIZE);
    }

    @Singleton
    @Provides
    public OkHttpClient providesOkHttpClient(Cache cache, @Named("Logger") Interceptor loggingInterceptor, @Named("UserAgent") Interceptor userAgentInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(userAgentInterceptor)
                .addNetworkInterceptor(loggingInterceptor)
                .cache(cache)
                .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);

        return builder.build();
    }

    @Singleton
    @Provides
    public Gson providesGson() {
        GsonBuilder gson = new GsonBuilder();
        return gson.create();
    }

    @Provides
    public Retrofit.Builder providesRetrofit(OkHttpClient client, Gson gson) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        return builder;
    }

    @Singleton
    @Provides
    public RedditService providesRedditService(Retrofit.Builder builder) {
        Retrofit retrofit = builder.baseUrl(BASE_URL).build();
        return retrofit.create(RedditService.class);
    }

    @Singleton
    @Provides
    public Picasso providesPicasso(OkHttpClient client) {
        return new Picasso.Builder(applicationContext)
                .loggingEnabled(true)
                .downloader(new OkHttp3Downloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Timber.e(exception, "Unable to load image from %s", uri.toString());
                    }
                })
                .build();
    }
}
