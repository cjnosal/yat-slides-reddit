package com.github.cjnosal.yats.network;

import android.content.SharedPreferences;
import android.util.Base64;

import com.github.cjnosal.yats.BuildConfig;
import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.services.RedditAuthService;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class AuthManager {

    public static final String UUID_KEY = "uuid";
    public static final String TOKEN_KEY = "token";
    public static final String EXPIRY_KEY = "expiry";
    public static final String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";

    SharedPreferences preferences;
    RedditAuthService redditService;

    String deviceId;

    String accessToken;
    Date expiry;

    public AuthManager(RedditAuthService redditService, SharedPreferences preferences) {
        this.redditService = redditService;
        this.preferences = preferences;

        accessToken = preferences.getString(TOKEN_KEY, null);
        expiry = new Date(preferences.getLong(EXPIRY_KEY, 0));

        // persist a unique device id
        deviceId = preferences.getString(UUID_KEY, null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            preferences.edit().putString(UUID_KEY, deviceId).apply();
        }
    }

    public boolean isAuthenticated() {
        return accessToken != null && new Date().before(expiry);
    }

    public String getOauthHeader() {
        if (isAuthenticated()) {
            return getAuthHeader("bearer", accessToken);
        }
        return null;
    }

    public Observable<AuthResponse> fetchAuthToken() {

        // no client secret for app-only auth
        final String authHeader = getAuthHeader("Basic", Base64.encodeToString((BuildConfig.OAUTH_CLIENT_ID + ":").getBytes(), Base64.NO_WRAP));

        RequestBody grantPart = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/plain");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(GRANT_TYPE.getBytes());
            }
        };

        RequestBody devicePart = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/plain");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(deviceId.getBytes());
            }
        };

        Observable<AuthResponse> authObservable = redditService.oauth2(grantPart, devicePart, authHeader)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(new Action1<AuthResponse>() {
                    @Override
                    public void call(AuthResponse response) {
                        Timber.d("Authentication complete: %s %d %s %s", response.access_token, response.expires_in, response.scope, response.token_type);
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.SECOND, response.expires_in);

                        expiry = calendar.getTime();
                        accessToken = response.access_token;

                        preferences.edit().putLong(EXPIRY_KEY, expiry.getTime()).putString(TOKEN_KEY, accessToken).apply();
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "Failed to authenticate");
                    }
                });

        return authObservable;
    }

    private String getAuthHeader(String type, String value) {
        return String.format("%s %s", type, value);
    }

}
