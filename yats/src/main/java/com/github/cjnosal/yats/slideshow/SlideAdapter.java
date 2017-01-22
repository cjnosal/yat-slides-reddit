package com.github.cjnosal.yats.slideshow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.cjnosal.yats.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class SlideAdapter extends PagerAdapter {

    Picasso picasso;

    private List<Slide> slides = new LinkedList<>();
    private Map<Slide, Palette> paletteMap = new HashMap<>();
    private Listener listener;

    @Inject
    public SlideAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setImages(List<Slide> slides) {
        this.slides = slides;
        notifyDataSetChanged();
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public Palette getPalette(int position) {
        if (position >= slides.size()) {
            return null;
        }
        Slide slide = slides.get(position);
        return paletteMap.get(slide);
    }

    @Override
    public int getCount() {
        return slides.size();
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
        final ViewGroup slideView =  (ViewGroup) inflater.inflate(R.layout.view_slide, container, false);
        final ImageView imageView = (ImageView) slideView.findViewById(R.id.slide_image);
        final TextView titleView = (TextView) slideView.findViewById(R.id.slide_title);
        final View titleContainer = slideView.findViewById(R.id.slide_title_container);
        final Slide slide = slides.get(position);
        titleView.setText(slide.getTitle());
        container.addView(slideView);

        slideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleContainer.setVisibility(titleContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        picasso.load(slide.getImageUrl()).fit().centerInside().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Palette.from(((BitmapDrawable)imageView.getDrawable()).getBitmap()).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        paletteMap.put(slide, palette);
                        if (listener != null) {
                            listener.onImageLoaded();
                        }
                    }
                });
            }

            @Override
            public void onError() {
                slides.remove(slide);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onImageFailed();
                }
            }
        });

        return new ViewHolder(slide, slideView, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder holder = (ViewHolder) object;
        container.removeView(holder.getView());
        paletteMap.remove(holder.getSlide());
        picasso.cancelRequest((ImageView)holder.getView().findViewById(R.id.slide_image));
    }

    @Override
    public int getItemPosition(Object object) {
        ViewHolder v = (ViewHolder)object;
        int index = slides.indexOf(v.getSlide());
        if (index == -1) {
            return POSITION_NONE;
        } else if (index == v.getPosition()) {
            return POSITION_UNCHANGED;
        } else {
            v.setPosition(index);
            return index;
        }
    }

    public CharSequence getPageTitle(int position) {
        return slides.get(position).getTitle();
    }

    private class ViewHolder {
        private Slide slide;
        private ViewGroup view;
        private int position;

        public ViewHolder(Slide slide, ViewGroup view, int position) {
            this.slide = slide;
            this.view = view;
            this.position = position;
        }

        public Slide getSlide() {
            return slide;
        }

        public ViewGroup getView() {
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
