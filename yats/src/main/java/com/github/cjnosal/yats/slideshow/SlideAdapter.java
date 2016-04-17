package com.github.cjnosal.yats.slideshow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.cjnosal.yats.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SlideAdapter extends PagerAdapter {

    Picasso picasso;

    private List<String> urls = new LinkedList<>();
    private Map<String, Palette> paletteMap = new HashMap<>();
    private Map<String, Request> requestMap = new HashMap<>();
    private Listener listener;

    public SlideAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setImages(List<String> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    public List<String> getImages() {
        return urls;
    }

    public Palette getPalette(int position) {
        if (position >= urls.size()) {
            return null;
        }
        String url = urls.get(position);
        return paletteMap.get(url);
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
        final ImageView view = (ImageView) inflater.inflate(R.layout.view_slide, container, false);
        container.addView(view);
        final String url = urls.get(position);
        picasso.load(url).fit().centerInside().into(view, new Callback() {
            @Override
            public void onSuccess() {
                Palette.from(((BitmapDrawable)view.getDrawable()).getBitmap()).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        paletteMap.put(url, palette);
                        if (listener != null) {
                            listener.onImageLoaded();
                        }
                    }
                });
            }

            @Override
            public void onError() {
                urls.remove(url);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onImageFailed();
                }
            }
        });
        return new ViewHolder(url, view, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder holder = (ViewHolder) object;
        container.removeView(holder.getView());
        paletteMap.remove(holder.getUrl());
        picasso.cancelRequest((ImageView)holder.getView());
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

    public interface Listener {
        void onImageLoaded();
        void onImageFailed();
    }
}
