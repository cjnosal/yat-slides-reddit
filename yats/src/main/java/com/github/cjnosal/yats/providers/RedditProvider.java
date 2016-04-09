package com.github.cjnosal.yats.providers;

import android.text.TextUtils;

import com.github.cjnosal.yats.config.UserSettings;
import com.github.cjnosal.yats.network.AuthManager;
import com.github.cjnosal.yats.network.models.AuthResponse;
import com.github.cjnosal.yats.network.models.subreddit.Link;
import com.github.cjnosal.yats.network.models.subreddit.SubredditSearchResponse;
import com.github.cjnosal.yats.network.services.RedditContentService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class RedditProvider {

    private static final String DEFAULT_SUB = "pics";
    private static final int NUM_IMAGES = 10;

    @Inject
    RedditContentService redditService;

    @Inject
    AuthManager authManager;

    @Inject
    UserSettings userSettings;

    String lastImage;
    Calendar startOfDay;
    Calendar endOfDay;

    @Inject
    public RedditProvider() {
        initDates();
    }

    private void initDates() {
        endOfDay = Calendar.getInstance();

        // move forward to midnight
        endOfDay.add(Calendar.DAY_OF_YEAR, 1);
        endOfDay.set(Calendar.HOUR_OF_DAY, 0);
        endOfDay.set(Calendar.MINUTE, 0);
        endOfDay.set(Calendar.SECOND, 0);

        // subtract a year
        endOfDay.add(Calendar.YEAR, -1);

        startOfDay = Calendar.getInstance();
        startOfDay.setTime(endOfDay.getTime());

        // subtract a day
        startOfDay.add(Calendar.DAY_OF_YEAR, -1);
    }

    public Observable<List<String>> getImageUrls() {

        if (authManager.isAuthenticated()) {
            return getUrlObservable();
        } else {
            return authManager.fetchAuthToken().flatMap(new Func1<AuthResponse, Observable<List<String>>>() {
                @Override
                public Observable<List<String>> call(AuthResponse authResponse) {
                    return getUrlObservable();
                }
            });
        }
    }

    private Observable<List<Link>> getLinkObservable() {
        return redditService.searchSubreddit(authManager.getOauthHeader(), DEFAULT_SUB, NUM_IMAGES, getQuery(), lastImage)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap(new Func1<SubredditSearchResponse, Observable<Link>>() {
                    @Override
                    public Observable<Link> call(SubredditSearchResponse subredditSearchResponse) {

                        String after = subredditSearchResponse.getListingData().getAfter();
                        if (TextUtils.isEmpty(after)) {
                            moveOneDayBack();
                            lastImage = null;
                        } else {
                            lastImage = after;
                        }
                        List<Link> links = subredditSearchResponse.getListingData().getLinks();
                        Timber.d("%d Reddit posts found", links.size());
                        return Observable.from(links);
                    }
                })
                .toList();
    }

    private void moveOneDayBack() {
        startOfDay.add(Calendar.DAY_OF_YEAR, -1);
        endOfDay.add(Calendar.DAY_OF_YEAR, -1);
    }

    private Observable<List<String>> getUrlObservable() {
        return getLinkObservable().flatMap(new Func1<List<Link>, Observable<String>>() {
            @Override
            public Observable<String> call(List<Link> links) {
                List<String> urls = new ArrayList<>(links.size());
                for (Link link : links) {
                    String url = getImageUrl(link);
                    if (!TextUtils.isEmpty(url)) {
                        urls.add(url);
                    }
                }
                Timber.d("%d image urls found", urls.size());
                return Observable.from(urls);
            }
        }).toList();
    }

    private String getImageUrl(Link link) {

        String linkUrl = link.getData().getUrl();

        String mediaUrl = null;
        if (link.getData().getMedia() != null) {
            mediaUrl = link.getData().getMedia().getOembed().getThumbnailUrl();
        }

        String previewUrl = null;
        if (link.getData().getPreview() != null) {
            previewUrl = link.getData().getPreview().getImages().get(0).getSource().getUrl();
        }

        String url = null;
        if (hasImageExtension(linkUrl)) {
            url = linkUrl;
            Timber.d("Using link url  %s", url);
        } else if (hasImageExtension(mediaUrl)) {
            url = mediaUrl;
            Timber.d("Using media url %s", url);
        } else if (hasImageExtension(previewUrl)) {
            url = previewUrl;
            Timber.d("Using preview url %s", url);
        } else if (tryDirectImgurLink(linkUrl)) {
            url = linkUrl + ".jpg";
            Timber.d("Trying direct imgur link %s", url);
        } else {
            Timber.d("Skipping %s", linkUrl);
        }

        return url;
    }

    private boolean tryDirectImgurLink(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost().contains("imgur.com")
                    && !uri.getPath().contains("/gallery/") // gallery link
                    && !uri.getPath().contains("/a/") // album link
                    && !uri.getPath().contains(","); // list of images
        } catch (URISyntaxException e) {
            Timber.e(e, "Unable to parse image link");
            return false;
        }
    }

    private boolean hasImageExtension(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        } else {
            String lower = url.toLowerCase();
            return lower.endsWith("png") || lower.endsWith("jpg") || lower.endsWith("jpeg");
        }
    }

    private String getQuery() {
        return getTimeQuery() + ' ' + getNsfwQuery();
    }

    private String getNsfwQuery() {
        return "nsfw:" + (userSettings.includeNsfw() ? '1' : '0');
    }

    private String getTimeQuery() {
        long endTime = endOfDay.getTimeInMillis() / 1000;
        long startTime = startOfDay.getTimeInMillis() / 1000;

        return String.format("timestamp:%1$s..%2$s", startTime, endTime);
    }
}
