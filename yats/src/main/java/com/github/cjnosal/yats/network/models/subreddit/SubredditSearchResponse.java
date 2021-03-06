
package com.github.cjnosal.yats.network.models.subreddit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class SubredditSearchResponse {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("data")
    @Expose
    private ListingData listingData;

    /**
     * 
     * @return
     *     The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * 
     * @param kind
     *     The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * 
     * @return
     *     The listingData
     */
    public ListingData getListingData() {
        return listingData;
    }

    /**
     * 
     * @param listingData
     *     The listingData
     */
    public void setListingData(ListingData listingData) {
        this.listingData = listingData;
    }

}
