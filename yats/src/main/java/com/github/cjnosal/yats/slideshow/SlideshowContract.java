package com.github.cjnosal.yats.slideshow;

import java.util.List;

public class SlideshowContract {

    public interface View {
        void displayImages(List<Slide> urls);
        void addImages(List<Slide> urls);
        void loadFailed();
    }

    public interface Presenter {
        void init(View view);
        void findImages(String subreddit, boolean reset);
    }

}
