package com.m.hyderabadmarketingapp.user_module.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m.hyderabadmarketingapp.user_module.models.CategoryModel;
import com.m.hyderabadmarketingapp.user_module.models.CategoryStoreModel;
import com.m.hyderabadmarketingapp.user_module.models.FeedsListResponce;
import com.m.hyderabadmarketingapp.user_module.models.LoginModel;
import com.m.hyderabadmarketingapp.user_module.models.OffersListResponce;
import com.m.hyderabadmarketingapp.user_module.models.StoreModel;
import com.m.hyderabadmarketingapp.user_module.models.TagsModel;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by madhu on 8/2/2018.
 */

public interface RetrofitApis {

        class Factory {
            public static RetrofitApis create(Context contextOfApplication) {

                // default time out is 15 seconds
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiUrls.BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                return retrofit.create(RetrofitApis.class);
            }
        }

    @FormUrlEncoded
    @POST("signup")
    Call<LoginModel> signupService(@Field("name") String name,@Field("mobile_number") String mobile_number,
    @Field("password") String password,@Field("device_type") String device_type,@Field("device_token") String device_token,@Field("type") String type);

    @FormUrlEncoded
    @POST("verifyOTP")
    Call<LoginModel> verifyOTPService(@Field("mobile_number") String mobile_number,@Field("otp") String otp);

    @FormUrlEncoded
    @POST("resendOTP")
    Call<LoginModel> resendOTPService(@Field("mobile_number") String mobile_number);

    @GET("categories")
    Call<CategoryModel> categoriesService();

    @FormUrlEncoded
    @POST("login")
    Call<LoginModel> loginService(@Field("mobile_number") String mobile_number,@Field("password") String password,@Field("device_type") String device_type,@Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("categorystores")
    Call<CategoryStoreModel> categorystoresService(@Field("type_of_store") String type_of_store, @Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("profile")
    Call<LoginModel> profileService(@Field("user_id") String user_id);

    @Multipart
    @POST("upload_profilepic")
    Call<LoginModel> uploadProfilepicservice(@Part MultipartBody.Part imageFile,@Part("user_id") String userid);

    @FormUrlEncoded
    @POST("storeDetails")
    Call<StoreModel> storeDetailsService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("latitude") String latitude,@Field("longitude") String longitude);

    @GET(ApiUrls.offers)
    Call<OffersListResponce> getOffers();

    @FormUrlEncoded
    @POST("merchantOffers")
    Call<OffersListResponce> merchantOffers(@Field("merchant_id") String merchant_id);

    @GET(ApiUrls.feed)
    Call<FeedsListResponce> getFeeds();

    @FormUrlEncoded
    @POST(ApiUrls.changePassword)
    Call<LoginModel> changePassword(@Field("oldpassword") String oldpassword,@Field("newpassword") String newpassword,@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST(ApiUrls.forgotPassword)
    Call<LoginModel> forgetPassword(@Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("likeMerchant")
    Call<LoginModel> likeMerchantService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("flag") String flag);

    @FormUrlEncoded
    @POST("addMerchantReview")
    Call<LoginModel> addMerchantReviewService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("review") String review);

    @FormUrlEncoded
    @POST("addMerchantRating")
    Call<LoginModel> addMerchantRatingService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("rating") String rating);

    @FormUrlEncoded
    @POST("updateProfile")
    Call<LoginModel> updateProfileService(@Field("user_id") String user_id,@Field("name") String name,@Field("email") String email,@Field("gender") String gender);


    @GET("tags")
    Call<TagsModel> tagsListService();
}

