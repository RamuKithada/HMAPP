package com.m.hyderabadmarketingapp.user_module.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.merchant_module.models.MLogin;
import com.m.hyderabadmarketingapp.merchant_module.presenter.RetrofitMerchantApis;
import com.m.hyderabadmarketingapp.user_module.models.LoginModel;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.mobile_number_edt)
    EditText mobile_number_edt;

    @BindView(R.id.reset_pass)
    Button reset_pass;

    @BindView(R.id.back_btn)
    ImageView back_btn;

    RetrofitApis retrofitApis;
    Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        reset_pass.setOnClickListener(this);
        retrofitApis=RetrofitApis.Factory.create(this);
        reset_pass.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(ForgetPasswordActivity.this);
        sessionManagement=new SessionManagement(ForgetPasswordActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.reset_pass:
        String mobilenumber=mobile_number_edt.getText().toString().trim();
        if(mobilenumber.trim().length()==0)
            callToast("Please enter your mobile number");
        else if(mobilenumber.trim().length()<4)
            callToast("Please enter valid mobile number");
       else
        {

            if(connectionDetector.isConnectingToInternet()) {
                if (sessionManagement.getUserType() == SessionManagement.AS_USER)
                    calltoserver(mobilenumber);
                else if (sessionManagement.getUserType() == SessionManagement.AS_MERCHANT)
                    callToMerchantServer(mobilenumber);
            }
            else
                callToast("You've no internet connection. Please try again.");
        }
                break;
        }
    }
    public void calltoserver(String mobilenumber){
        Call<LoginModel> call= retrofitApis.forgetPassword(mobilenumber);
        dialog.show();
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if(response.isSuccessful()){
                    LoginModel model=response.body();
                    Toast.makeText(ForgetPasswordActivity.this,model.getResult(),Toast.LENGTH_SHORT).show();
                    if(model.getStatus().equals("1"))
                        finish();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });

    }
    public void callToMerchantServer(String mobilenumber){
        Call<MLogin> call= RetrofitMerchantApis.Factory.create(this).forgetPassword(mobilenumber);
        dialog.show();
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if(response.isSuccessful()){
                    MLogin model=response.body();
                    Toast.makeText(ForgetPasswordActivity.this,model.getResult(),Toast.LENGTH_SHORT).show();
                    if(model.getStatus()==1)
                        finish();
                }else if(response.code()==400){
                    Gson gson=new Gson();
                    MLogin model= null;
                    try {
                        model = gson.fromJson(response.errorBody().string(),MLogin.class);
                        if(model==null)return;
                        Toast.makeText(ForgetPasswordActivity.this,model.getResult(),Toast.LENGTH_SHORT).show();
                        if(model.getStatus()==1)
                            finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });

    }
    private void callToast(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
