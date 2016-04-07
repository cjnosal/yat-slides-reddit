package com.github.cjnosal.yats.config;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class UserSettings {
    private static final String NSFW_KEY = "nsfw";

    SharedPreferences preferences;

    @Inject
    public UserSettings(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void setIncludeNsfw(boolean nsfw) {
        preferences.edit().putBoolean(NSFW_KEY, nsfw).apply();
    }

    public boolean includeNsfw() {
        return preferences.getBoolean(NSFW_KEY, false);
    }
}
