package com.github.cjnosal.yats.slideshow;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.cjnosal.yats.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class SlideAdapter extends PagerAdapter {

    @Inject
    Picasso picasso;

    private List<String> urls = new LinkedList<>();

    public void setImages(List<String> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    public List<String> getImages() {
        return urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        ImageView view = (ImageView) inflater.inflate(R.layout.view_slide, container, false);
        container.addView(view);
        final String url = urls.get(position);
        picasso.load(url).fit().centerInside().into(view, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                urls.remove(url);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView)object);
    }

    // TODO post title
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
