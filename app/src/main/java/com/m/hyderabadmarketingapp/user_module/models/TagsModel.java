package com.m.hyderabadmarketingapp.user_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TagsModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public class Tag {

        @SerializedName("tag_id")
        @Expose
        private String tagId;
        @SerializedName("tag_name")
        @Expose
        private String tagName;

        public String getTagId() {
            return tagId;
        }

        public void setTagId(String tagId) {
            this.tagId = tagId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public String toString() { return tagName; }
    }
}
