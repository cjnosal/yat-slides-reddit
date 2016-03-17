package com.github.cjnosal.yats.slideshow;

import android.os.Bundle;

import java.util.List;

public class SlideshowContract {

    public interface View {
        void displayImages(List<String> urls);
    }

    public interface UserActionListener {
        void fetchUrls();
        void onSaveInstanceState(Bundle outState);
    }

}
