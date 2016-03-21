
package com.github.cjnosal.yats.network.models.subreddit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ListingData {

    @SerializedName("facets")
    @Expose
    private Facets facets;
    @SerializedName("modhash")
    @Expose
    private String modhash;
    @SerializedName("children")
    @Expose
    private List<Link> links = new ArrayList<Link>();
    @SerializedName("after")
    @Expose
    private String after;
    @SerializedName("before")
    @Expose
    private Object before;

    /**
     * 
     * @return
     *     The facets
     */
    public Facets getFacets() {
        return facets;
    }

    /**
     * 
     * @param facets
     *     The facets
     */
    public void setFacets(Facets facets) {
        this.facets = facets;
    }

    /**
     * 
     * @return
     *     The modhash
     */
    public String getModhash() {
        return modhash;
    }

    /**
     * 
     * @param modhash
     *     The modhash
     */
    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    /**
     * 
     * @return
     *     The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The after
     */
    public String getAfter() {
        return after;
    }

    /**
     * 
     * @param after
     *     The after
     */
    public void setAfter(String after) {
        this.after = after;
    }

    /**
     * 
     * @return
     *     The before
     */
    public Object getBefore() {
        return before;
    }

    /**
     * 
     * @param before
     *     The before
     */
    public void setBefore(Object before) {
        this.before = before;
    }

}
