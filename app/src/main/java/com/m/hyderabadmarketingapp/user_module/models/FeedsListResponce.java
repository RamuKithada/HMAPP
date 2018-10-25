
package com.m.hyderabadmarketingapp.user_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedsListResponce {

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("status")
    @Expose
    private Long status;
    @SerializedName("feed")
    @Expose
    private List<Feed> feed = null;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public List<Feed> getFeed() {
        return feed;
    }

    public void setFeed(List<Feed> feed) {
        this.feed = feed;
    }

}
