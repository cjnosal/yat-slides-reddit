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
        return ((ViewHolder)object).getView() == view;
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
        return new ViewHolder(url, view, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((ViewHolder)object).getView());
    }

    @Override
    public int getItemPosition(Object object) {
        ViewHolder v = (ViewHolder)object;
        int index = urls.indexOf(v.getUrl());
        if (index == -1) {
            return POSITION_NONE;
        } else if (index == v.getPosition()) {
            return POSITION_UNCHANGED;
        } else {
            v.setPosition(index);
            return index;
        }
    }

    // TODO post title
    public CharSequence getPageTitle(int position) {
        return null;
    }

    private class ViewHolder {
        private String url;
        private View view;
        private int position;

        public ViewHolder(String url, View view, int position) {
            this.url = url;
            this.view = view;
            this.position = position;
        }

        public String getUrl() {
            return url;
        }

        public View getView() {
            return view;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
