package com.github.cjnosal.yats.slideshow;

import java.io.Serializable;

public class Slide implements Serializable {
    private String image;
    private String title;
    private String description;

    public Slide(String image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImageUrl() {
        return this.image;
    }

    public String getTitle() {
        return this.title;
    }
}
