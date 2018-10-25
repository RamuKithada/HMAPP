package com.m.hyderabadmarketingapp.user_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.autoreadotp.Common;
import com.m.hyderabadmarketingapp.autoreadotp.SMSListener;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.merchant_module.activities.MerchantHomeActivity;
import com.m.hyderabadmarketingapp.merchant_module.models.MLogin;
import com.m.hyderabadmarketingapp.merchant_module.models.Merchantinfo;
import com.m.hyderabadmarketingapp.merchant_module.presenter.MerchantApiUrls;
import com.m.hyderabadmarketingapp.merchant_module.presenter.RetrofitMerchantApis;
import com.m.hyderabadmarketingapp.user_module.models.LoginModel;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.RECEIVE_SMS;

public class OtpScreen extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.et_otp)
    EditText et_otp;
    @BindView(R.id.layout_confirmotp)
    RelativeLayout layout_confirmotp;
    @BindView(R.id.layout_resendotp)
    RelativeLayout layout_resendotp;
    @BindView(R.id.layout_enternumber)
    RelativeLayout layout_enternumber;
    @BindView(R.id.tv_mobilenumber)
    TextView tv_mobilenumber;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private String mobilenumber;
    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_screen);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        layout_enternumber.setOnClickListener(this);
        layout_resendotp.setOnClickListener(this);
        layout_confirmotp.setOnClickListener(this);
        mobilenumber=getIntent().getStringExtra("mobilenumber");
         tv_mobilenumber.setText(mobilenumber);

        dialog = new Dialog(OtpScreen.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        sessionManagement=new SessionManagement(OtpScreen.this);
        connectionDetector=new ConnectionDetector(OtpScreen.this);

        if(!CheckingPermissionIsEnabledOrNot())
            RequestMultiplePermission();

        SMSListener.bindListener(new Common.OTPListener() {
            @Override
            public void onOTPReceived(String extractedOTP) {
                Log.e("extracted otp",""+extractedOTP);
                et_otp.setText(extractedOTP);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.layout_confirmotp:

                String otp=et_otp.getText().toString().trim();
                if(otp.length()<6)
                    callToast("Please enter 6 digit otp");
                else{
                    if(connectionDetector.isConnectingToInternet()) {
                        if (sessionManagement.getUserType() == SessionManagement.AS_USER)
                            verifyOtpService(mobilenumber, otp);
                        else if (sessionManagement.getUserType() == SessionManagement.AS_MERCHANT)
                            verifyOtpMerchantService(mobilenumber, otp);
                    }
                    else
                        callToast("You've no internet connection. Please try again.");
                }

                break;
            case R.id.layout_resendotp:
                if(connectionDetector.isConnectingToInternet()) {
                    if (sessionManagement.getUserType() == SessionManagement.AS_USER)
                        resendOtpService(mobilenumber);
                    else if (sessionManagement.getUserType() == SessionManagement.AS_MERCHANT)
                       resendOtpMerchantService(mobilenumber);
                }
                else
                    callToast("You've no internet connection. Please try again.");
                break;
            case R.id.layout_enternumber:
                finish();
                break;
        }
    }

    private void callToast(String msg)
    {
        Toast.makeText(OtpScreen.this,msg,Toast.LENGTH_SHORT).show();
    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int readsms = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        return readsms == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(OtpScreen.this, new String[]
                {
                        RECEIVE_SMS,
                }, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 100:

                if (grantResults.length > 0) {

                    boolean readsms_permission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readsms_permission) {
                        callToast("Auto Read OTP Permission Granted");
                    }
                    else {
                        callToast("Auto Read OTP Permission Denied");
                    }
                }

                break;
        }
    }

    private void verifyOtpService(final String mobile_number, String otp)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.verifyOTPService(mobile_number,otp);
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

                        Intent intent=new Intent(OtpScreen.this,HomeActivity.class);
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
            }
        });

    }
    private void verifyOtpMerchantService(final String mobile_number, String otp)
    {
        dialog.show();
        RetrofitMerchantApis retrofitMerchantApis =RetrofitMerchantApis.Factory.create(this);
        Call<MLogin> call = retrofitMerchantApis.verifyOTPService(mobile_number,otp);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
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
                        Intent intent=new Intent(OtpScreen.this,MerchantHomeActivity.class);
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
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }

    private void resendOtpService(final String mobile_number)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.resendOTPService(mobile_number);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }


    private void resendOtpMerchantService(final String mobile_number)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<MLogin> call = service.resendOTPService(mobile_number);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }


    @Override
    protected void onDestroy() {
        SMSListener.unbindListener();
        super.onDestroy();
    }
}
