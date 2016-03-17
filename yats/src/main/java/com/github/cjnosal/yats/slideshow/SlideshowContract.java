package com.github.cjnosal.yats.slideshow;

import java.util.List;

public class SlideshowContract {

    public interface View {
        void displayImages(List<String> urls);
    }

    public interface UserActionListener {
        void fetchUrls();
    }

}
