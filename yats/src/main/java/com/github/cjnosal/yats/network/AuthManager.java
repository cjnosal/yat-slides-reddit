package com.github.cjnosal.yats.network;

import android.content.SharedPreferences;
import android.util.Base64;

import com.github.cjnosal.yats.BuildConfig;
import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.services.RedditService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import rx.Observable;

@Singleton
public class AuthManager {

    public static final String UUID_KEY = "uuid";
    public static final String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";

    SharedPreferences preferences;
    RedditService redditService;

    String deviceId;

    @Inject
    public AuthManager(RedditService redditService, SharedPreferences preferences) {
        this.redditService = redditService;
        this.preferences = preferences;

        // persist a unique device id
        deviceId = preferences.getString(UUID_KEY, null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            preferences.edit().putString(UUID_KEY, deviceId).apply();
        }
    }

    public Observable<AuthResponse> fetchAuthToken() {

        // no client secret for app-only auth
        final String authHeader = "Basic " + Base64.encodeToString((BuildConfig.OAUTH_CLIENT_ID + ":").getBytes(), Base64.NO_WRAP);

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

        return redditService.oauth2(grantPart, devicePart, authHeader);
    }

}
