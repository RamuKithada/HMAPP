package com.m.hyderabadmarketingapp.user_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryStoreModel {

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("categorystores")
    @Expose
    private List<Categorystore> categorystores = null;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Categorystore> getCategorystores() {
        return categorystores;
    }

    public void setCategorystores(List<Categorystore> categorystores) {
        this.categorystores = categorystores;
    }

    public Categorystore getStoreObject()
    {
        return new Categorystore();
    }

    public class Categorystore {

        @SerializedName("merchant_id")
        @Expose
        private String merchantId;
        @SerializedName("merchant_name")
        @Expose
        private String merchantName;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("mobile_number")
        @Expose
        private String mobileNumber;
        @SerializedName("type_of_store")
        @Expose
        private String typeOfStore;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("device_token")
        @Expose
        private String deviceToken;
        @SerializedName("facebook_id")
        @Expose
        private String facebookId;
        @SerializedName("google_id")
        @Expose
        private String googleId;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("rating")
        @Expose
        private String rating;
        @SerializedName("merchant_photos")
        @Expose
        private List<String> merchantPhotos = null;
        @SerializedName("merchant_banner")
        @Expose
        private String merchantBanner;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("otp")
        @Expose
        private String otp;
        @SerializedName("open_time")
        @Expose
        private String openTime;
        @SerializedName("close_time")
        @Expose
        private String closeTime;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("website")
        @Expose
        private String website;
        @SerializedName("distance")
        @Expose
        private String distance;
        @SerializedName("offers")
        @Expose
        private Integer offers;
        @SerializedName("likecount")
        @Expose
        private Integer likecount;

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getMerchantName() {
            return merchantName;
        }

        public void setMerchantName(String merchantName) {
            this.merchantName = merchantName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getTypeOfStore() {
            return typeOfStore;
        }

        public void setTypeOfStore(String typeOfStore) {
            this.typeOfStore = typeOfStore;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getFacebookId() {
            return facebookId;
        }

        public void setFacebookId(String facebookId) {
            this.facebookId = facebookId;
        }

        public String getGoogleId() {
            return googleId;
        }

        public void setGoogleId(String googleId) {
            this.googleId = googleId;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public List<String> getMerchantPhotos() {
            return merchantPhotos;
        }

        public void setMerchantPhotos(List<String> merchantPhotos) {
            this.merchantPhotos = merchantPhotos;
        }

        public String getMerchantBanner() {
            return merchantBanner;
        }

        public void setMerchantBanner(String merchantBanner) {
            this.merchantBanner = merchantBanner;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getOpenTime() {
            return openTime;
        }

        public void setOpenTime(String openTime) {
            this.openTime = openTime;
        }

        public String getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(String closeTime) {
            this.closeTime = closeTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public Integer getOffers() {
            return offers;
        }

        public void setOffers(Integer offers) {
            this.offers = offers;
        }

        public Integer getLikecount() {
            return likecount;
        }

        public void setLikecount(Integer likecount) {
            this.likecount = likecount;
        }

    }
}
