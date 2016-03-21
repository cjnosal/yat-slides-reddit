
package com.github.cjnosal.yats.network.models.subreddit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Image {

    @SerializedName("source")
    @Expose
    private ImageData source;
    @SerializedName("resolutions")
    @Expose
    private List<ImageData> imageDatas = new ArrayList<ImageData>();
    @SerializedName("variants")
    @Expose
    private Variants variants;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * 
     * @return
     *     The source
     */
    public ImageData getSource() {
        return source;
    }

    /**
     * 
     * @param source
     *     The source
     */
    public void setSource(ImageData source) {
        this.source = source;
    }

    /**
     * 
     * @return
     *     The imageDatas
     */
    public List<ImageData> getImageDatas() {
        return imageDatas;
    }

    /**
     * 
     * @param imageDatas
     *     The imageDatas
     */
    public void setImageDatas(List<ImageData> imageDatas) {
        this.imageDatas = imageDatas;
    }

    /**
     * 
     * @return
     *     The variants
     */
    public Variants getVariants() {
        return variants;
    }

    /**
     * 
     * @param variants
     *     The variants
     */
    public void setVariants(Variants variants) {
        this.variants = variants;
    }

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

}
