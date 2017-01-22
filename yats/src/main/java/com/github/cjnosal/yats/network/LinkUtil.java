package com.github.cjnosal.yats.network;

import android.text.TextUtils;

import com.github.cjnosal.yats.network.models.subreddit.Link;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import timber.log.Timber;

public class LinkUtil {

    @Inject
    public LinkUtil() {}

    public String getImageUrl(Link link) {

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
}
