package com.m.hyderabadmarketingapp.merchant_module.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m.hyderabadmarketingapp.merchant_module.models.MLogin;
import com.m.hyderabadmarketingapp.user_module.models.CategoryStoreModel;
import com.m.hyderabadmarketingapp.user_module.models.LoginModel;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by madhu on 8/2/2018.
 */

public interface RetrofitMerchantApis {

        class Factory {
            public static RetrofitMerchantApis create(Context contextOfApplication) {

                // default time out is 15 seconds
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .build();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MerchantApiUrls.BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                return retrofit.create(RetrofitMerchantApis.class);
            }
        }

    @FormUrlEncoded
    @POST("signup")
    Call<MLogin> signupService(@Field("type") String type,
                                   @Field("name") String name,
                                   @Field("mobile_number") String mobile_number,
                                   @Field("password") String password,
                                   @Field("device_type") String device_type,
                                   @Field("device_token") String device_token,
                                   @Field("latitude") String latitude,
                                   @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("verifyotp")
    Call<MLogin> verifyOTPService(@Field("mobile_number") String mobile_number, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("resendotp")
    Call<MLogin> resendOTPService(@Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("login")
    Call<MLogin> loginService(@Field("mobile_number") String mobile_number,
                              @Field("password") String password,
                              @Field("device_type") String device_type,
                              @Field("device_token") String device_token,
                              @Field("latitude") String latitude,
                              @Field("longitude") String longitude);


    @FormUrlEncoded
    @POST("profile")
    Call<LoginModel> profileService(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST(MerchantApiUrls.changePassword)
    Call<MLogin> changePassword(@Field("oldpassword") String oldpassword,@Field("newpassword") String newpassword,@Field("merchant_id") String user_id);

    @FormUrlEncoded
    @POST(ApiUrls.forgotPassword)
    Call<MLogin> forgetPassword(@Field("mobile_number") String mobile_number);
}

