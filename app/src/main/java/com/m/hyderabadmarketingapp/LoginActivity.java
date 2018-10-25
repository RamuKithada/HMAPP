package com.m.hyderabadmarketingapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.merchant_module.activities.MerchantHomeActivity;
import com.m.hyderabadmarketingapp.merchant_module.models.MLogin;
import com.m.hyderabadmarketingapp.merchant_module.models.Merchantinfo;
import com.m.hyderabadmarketingapp.merchant_module.presenter.RetrofitMerchantApis;
import com.m.hyderabadmarketingapp.user_module.activities.ForgetPasswordActivity;
import com.m.hyderabadmarketingapp.user_module.activities.HomeActivity;
import com.m.hyderabadmarketingapp.user_module.models.LoginModel;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.layout_fb)
    RelativeLayout layout_fb;
    @BindView(R.id.layout_google)
    RelativeLayout layout_google;
    @BindView(R.id.et_mobilenumber)
    EditText et_mobilenumber;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.layout_signin)
    RelativeLayout layout_signin;
    @BindView(R.id.tv_createan_account)
    TextView tv_createan_account;
    @BindView(R.id.tv_forgotpassword)
    TextView tv_forgotpassword;

    @BindView(R.id.user)
    TextView loginAsUser;

    @BindView(R.id.merchant)
    TextView loginAsMerchant;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private SessionManagement sessionManagement;
    private String lattitude="17.3933",longnitude="75.5334";

    View.OnClickListener loginSelecter=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           switch (view.getId()){
               case R.id.user:
                   loginAsUser.setBackgroundResource(R.color.colorOrangebutton);
                   loginAsMerchant.setBackgroundResource(R.color.trns);
                   sessionManagement.setUserType(SessionManagement.AS_USER);

                   break;
               case R.id.merchant:
                   loginAsMerchant.setBackgroundResource(R.color.colorOrangebutton);
                   loginAsUser.setBackgroundResource(R.color.trns);
                   sessionManagement.setUserType(SessionManagement.AS_MERCHANT);
                   break;
           }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        layout_signin.setOnClickListener(this);
        tv_createan_account.setOnClickListener(this);
        tv_forgotpassword.setOnClickListener(this);
        layout_fb.setOnClickListener(this);
        layout_google.setOnClickListener(this);
        loginAsUser.setOnClickListener(loginSelecter);
        loginAsMerchant.setOnClickListener(loginSelecter);

        dialog = new Dialog(LoginActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(LoginActivity.this);
        sessionManagement=new SessionManagement(LoginActivity.this);
        sessionManagement.setUserType(-1);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.layout_signin:

                if(sessionManagement.getUserType()>0)
                    checkLoginCreds();
                else
                    callToast("Please select user or merchant");
                break;

            case R.id.tv_createan_account:
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_forgotpassword:
                if(sessionManagement.getUserType()>0) {
                    Intent forgotpwd = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    startActivity(forgotpwd);
                }else{
                    callToast("Please select user or merchant");
                }
                break;
            case R.id.layout_fb:
                callToast("layout_fb");
                break;

            case R.id.layout_google:
                callToast("layout_google");
                break;
        }
    }

    private void checkLoginCreds()
    {
        String mobilenumber=et_mobilenumber.getText().toString();
        String password=et_password.getText().toString();
        if(mobilenumber.trim().length()==0)
            callToast("Please enter your mobile number");
        else if(mobilenumber.trim().length()<4)
            callToast("Please enter valid mobile number");
        else if(password.trim().length()==0)
            callToast("Please enter password");
        else if(password.trim().length()<6)
            callToast("Please enter atleast 6 digits password");
        else {
            if(connectionDetector.isConnectingToInternet())
            {
                if(sessionManagement.getUserType()==SessionManagement.AS_USER)
                        loginService(mobilenumber,password,"android","123");
                else if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT)
                        loginAsMerchantService(mobilenumber,password,"android","123",lattitude,longnitude);
            }
            else
                callToast("You've no internet connection. Please try again.");
        }
    }
    private void loginService(final String mobile_number, String password, String device_type, String device_token)
    {
        dialog.show();


        RetrofitApis service = RetrofitApis.Factory.create(this);
        Call<LoginModel> call = service.loginService(mobile_number,password,device_type,device_token);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    if(model.getStatus().equalsIgnoreCase("1"))
                    {
                        sessionManagement.setValuetoPreference(SessionManagement.NAME,model.getData().getName());
                        sessionManagement.setValuetoPreference(SessionManagement.MOBILE,model.getData().getMobileNumber());
                        sessionManagement.setValuetoPreference(SessionManagement.DEVICETOKEN,model.getData().getDeviceToken());
                        sessionManagement.setValuetoPreference(SessionManagement.USERID,model.getData().getUserId());
                        sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID,model.getData().getFacebookId());
                        sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID,model.getData().getGoogleId());
                        sessionManagement.setValuetoPreference(SessionManagement.PROFILEIMAGE,model.getData().getProfileImage());
                        sessionManagement.setValuetoPreference(SessionManagement.EMAIL,model.getData().getEmail());
                        sessionManagement.setValuetoPreference(SessionManagement.GENDER,model.getData().getGender());
                        sessionManagement.setBooleanValuetoPreference(sessionManagement.ISLOGIN,true);

                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                        finishAffinity();
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
                Log.e("get response","onFailure");
            }
        });

    }
    private void loginAsMerchantService(final String mobile_number, String password, String device_type, String device_token,String lattitude,String longnitude)
    {
        dialog.show();


        RetrofitMerchantApis service = RetrofitMerchantApis.Factory.create(this);
        Call<MLogin> call = service.loginService(mobile_number,password,device_type,device_token,lattitude,longnitude);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                if(response.isSuccessful())
                {

                    MLogin model=response.body();
                    if(model.getStatus()==1)
                    {
                        Merchantinfo merchantinfo= model.getMerchantinfo();
                        if(merchantinfo!=null) {
                            sessionManagement.setValuetoPreference(SessionManagement.NAME, merchantinfo.getMerchantName());
                            sessionManagement.setValuetoPreference(SessionManagement.MOBILE, merchantinfo.getMobileNumber());
                            sessionManagement.setValuetoPreference(SessionManagement.DEVICETOKEN, merchantinfo.getDeviceToken());
                            sessionManagement.setValuetoPreference(SessionManagement.USERID, merchantinfo.getMerchantId());
                            sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID, merchantinfo.getFacebookId());
                            sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID, merchantinfo.getGoogleId());
                            sessionManagement.setValuetoPreference(SessionManagement.PROFILEIMAGE, merchantinfo.getMerchantPhotos());
                            sessionManagement.setValuetoPreference(SessionManagement.EMAIL, merchantinfo.getEmail());
                            sessionManagement.setBooleanValuetoPreference(sessionManagement.ISLOGIN, true);
                        }
                        Intent intent = new Intent(LoginActivity.this, MerchantHomeActivity.class);
                        startActivity(intent);
                        finish();
                        finishAffinity();

                    }
                    else {
                        callToast(model.getResult());
                    }
                }else if(response.code()==400){
                   try{String body= response.errorBody().string();
                      Log.e("ResponseBody",""+body);
                       Gson gson=new Gson();
                       MLogin model= gson.fromJson(body,MLogin.class);
                       if(model.getStatus()==1)
                       {
                           Merchantinfo merchantinfo= model.getMerchantinfo();
                           if(merchantinfo!=null) {
                               sessionManagement.setValuetoPreference(SessionManagement.NAME, merchantinfo.getMerchantName());
                               sessionManagement.setValuetoPreference(SessionManagement.MOBILE, merchantinfo.getMobileNumber());
                               sessionManagement.setValuetoPreference(SessionManagement.DEVICETOKEN, merchantinfo.getDeviceToken());
                               sessionManagement.setValuetoPreference(SessionManagement.USERID, merchantinfo.getMerchantId());
                               sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID, merchantinfo.getFacebookId());
                               sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID, merchantinfo.getGoogleId());
                               sessionManagement.setValuetoPreference(SessionManagement.PROFILEIMAGE, merchantinfo.getMerchantPhotos());
                               sessionManagement.setValuetoPreference(SessionManagement.EMAIL, merchantinfo.getEmail());
                               sessionManagement.setBooleanValuetoPreference(sessionManagement.ISLOGIN, true);
                           }
                           Intent intent = new Intent(LoginActivity.this, MerchantHomeActivity.class);
                           startActivity(intent);
                           finish();
                           finishAffinity();

                       }
                       else {
                           callToast(model.getResult());
                       }


                   }
                   catch (IOException e){
                       e.printStackTrace();
                   }
                   catch (JsonSyntaxException e){
                       e.printStackTrace();
                   }
                }


            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
                Log.e("get response","onFailure");
            }
        });

    }
    private void callToast(String msg)
    {
        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
