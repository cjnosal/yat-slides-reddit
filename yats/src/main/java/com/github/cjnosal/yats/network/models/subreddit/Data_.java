
package com.github.cjnosal.yats.network.models.subreddit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data_ {

    @SerializedName("domain")
    @Expose
    public String domain;
    @SerializedName("banned_by")
    @Expose
    public Object bannedBy;
    @SerializedName("media_embed")
    @Expose
    public MediaEmbed mediaEmbed;
    @SerializedName("subreddit")
    @Expose
    public String subreddit;
    @SerializedName("selftext_html")
    @Expose
    public String selftextHtml;
    @SerializedName("selftext")
    @Expose
    public String selftext;
    @SerializedName("likes")
    @Expose
    public Object likes;
    @SerializedName("suggested_sort")
    @Expose
    public Object suggestedSort;
    @SerializedName("user_reports")
    @Expose
    public List<Object> userReports = new ArrayList<Object>();
    @SerializedName("secure_media")
    @Expose
    public Object secureMedia;
    @SerializedName("link_flair_text")
    @Expose
    public String linkFlairText;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("from_kind")
    @Expose
    public Object fromKind;
    @SerializedName("gilded")
    @Expose
    public int gilded;
    @SerializedName("archived")
    @Expose
    public boolean archived;
    @SerializedName("clicked")
    @Expose
    public boolean clicked;
    @SerializedName("report_reasons")
    @Expose
    public Object reportReasons;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("media")
    @Expose
    public Media media;
    @SerializedName("score")
    @Expose
    public int score;
    @SerializedName("approved_by")
    @Expose
    public Object approvedBy;
    @SerializedName("over_18")
    @Expose
    public boolean over18;
    @SerializedName("hidden")
    @Expose
    public boolean hidden;
    @SerializedName("num_comments")
    @Expose
    public int numComments;
    @SerializedName("thumbnail")
    @Expose
    public String thumbnail;
    @SerializedName("subreddit_id")
    @Expose
    public String subredditId;
    @SerializedName("hide_score")
    @Expose
    public boolean hideScore;
//    @SerializedName("edited")
//    @Expose
//    public boolean edited; // false if not edited, timestamp in seconds if edited
    @SerializedName("link_flair_css_class")
    @Expose
    public String linkFlairCssClass;
    @SerializedName("author_flair_css_class")
    @Expose
    public Object authorFlairCssClass;
    @SerializedName("downs")
    @Expose
    public int downs;
    @SerializedName("secure_media_embed")
    @Expose
    public SecureMediaEmbed secureMediaEmbed;
    @SerializedName("saved")
    @Expose
    public boolean saved;
    @SerializedName("removal_reason")
    @Expose
    public Object removalReason;
    @SerializedName("stickied")
    @Expose
    public boolean stickied;
    @SerializedName("from")
    @Expose
    public Object from;
    @SerializedName("is_self")
    @Expose
    public boolean isSelf;
    @SerializedName("from_id")
    @Expose
    public Object fromId;
    @SerializedName("permalink")
    @Expose
    public String permalink;
    @SerializedName("locked")
    @Expose
    public boolean locked;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("created")
    @Expose
    public double created;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("author_flair_text")
    @Expose
    public Object authorFlairText;
    @SerializedName("quarantine")
    @Expose
    public boolean quarantine;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("created_utc")
    @Expose
    public double createdUtc;
    @SerializedName("distinguished")
    @Expose
    public Object distinguished;
    @SerializedName("mod_reports")
    @Expose
    public List<Object> modReports = new ArrayList<Object>();
    @SerializedName("visited")
    @Expose
    public boolean visited;
    @SerializedName("num_reports")
    @Expose
    public Object numReports;
    @SerializedName("ups")
    @Expose
    public int ups;

}
