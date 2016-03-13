package com.github.cjnosal.yats.slideshow;

import android.os.Bundle;
import android.widget.ImageView;

import com.github.cjnosal.yats.R;
import com.github.cjnosal.yats.YATSApplication;
import com.github.cjnosal.yats.modules.ApplicationComponent;
import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.services.RedditService;
import com.github.cjnosal.yats.slideshow.modules.DaggerSlideshowComponent;
import com.github.cjnosal.yats.slideshow.modules.SlideshowComponent;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SlideshowActivity extends RxAppCompatActivity implements SlideshowContract.View {

    @Bind(R.id.image)
    ImageView image;

    @Inject
    RedditService redditService;

    @Inject
    AuthManager authManager;

    SlideshowContract.UserActionListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        ButterKnife.bind(this);

        ApplicationComponent applicationComponent = ((YATSApplication) getApplication()).getApplicationComponent();
        SlideshowComponent slideshowComponent = DaggerSlideshowComponent.builder().applicationComponent(applicationComponent).build();
        slideshowComponent.inject(this);

        listener = new SlideshowPresenter(redditService, authManager, this);
        listener.fetchImages();
    }

    @Override
    public void displayImages(List<String> urls) {
        Picasso.with(this).load(urls.get(0)).into(image);
    }
}
