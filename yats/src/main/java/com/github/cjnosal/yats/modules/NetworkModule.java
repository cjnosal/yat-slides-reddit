package com.github.cjnosal.yats.modules;

import android.content.Context;
import android.net.Uri;

import com.github.cjnosal.yats.BuildConfig;
import com.github.cjnosal.yats.network.services.RedditAuthService;
import com.github.cjnosal.yats.network.services.RedditContentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
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
public class NetworkModule {

    private static final int NETWORK_TIMEOUT = 15; // seconds
    private static final String OKHTTP = "OkHttp";
    private static final int CACHE_SIZE = 25 * 1024 * 1024; // 25MB
    private static final String BASE_CONTENT_URL = "https://oauth.reddit.com/";
    private static final String BASE_AUTH_URL = "https://www.reddit.com/";

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
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
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
    @Named("BlackList")
    public Interceptor providesBlackListInterceptor() {
        return new Interceptor() {
            private final List<String> removedImages = Arrays.asList(
                    "i.imgur.com/removed.png",
                    "s.yimg.com/pw/images/en-us/photo_unavailable_l.png"
            );

            @Override
            public Response intercept(Chain chain) throws IOException {

                Response response = chain.proceed(chain.request());
                HttpUrl url = response.request().url();
                Timber.d("Loaded %s", url);

                for (String blacklistedUrl : removedImages) {
                    if (String.format("%s%s", url.host(), url.encodedPath()).equals(blacklistedUrl)) {
                        return response.newBuilder().code(410).message("Blacklisted").build();
                    }
                }

                return response;
            }
        };
    }

    @Singleton
    @Provides
    public Cache providesCache(Context applicationContext) {
        File cacheDir = applicationContext.getCacheDir();
        cacheDir = new File(cacheDir, OKHTTP);
        //noinspection ResultOfMethodCallIgnored
        cacheDir.mkdirs();
        return new Cache(cacheDir, CACHE_SIZE);
    }

    @Singleton
    @Provides
    public OkHttpClient providesOkHttpClient(Cache cache,
                                             @Named("Logger") Interceptor loggingInterceptor,
                                             @Named("UserAgent") Interceptor userAgentInterceptor,
                                             @Named("BlackList") Interceptor blackListInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(blackListInterceptor)
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
    public RedditContentService providesRedditContentService(Retrofit.Builder builder) {
        Retrofit retrofit = builder.baseUrl(BASE_CONTENT_URL).build();
        return retrofit.create(RedditContentService.class);
    }

    @Singleton
    @Provides
    public RedditAuthService providesRedditAuthService(Retrofit.Builder builder) {
        Retrofit retrofit = builder.baseUrl(BASE_AUTH_URL).build();
        return retrofit.create(RedditAuthService.class);
    }

    @Singleton
    @Provides
    public Picasso providesPicasso(Context applicationContext, OkHttpClient client) {
        Picasso picasso = new Picasso.Builder(applicationContext)
                .loggingEnabled(false)
                .downloader(new OkHttp3Downloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Timber.e(exception, "Unable to load image from %s", uri.toString());
                    }
                })
                .build();
        Picasso.setSingletonInstance(picasso);
        return picasso;
    }
}
