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

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

        @BindView(R.id.old_pass)
        EditText old_pass;

        @BindView(R.id.new_pass)
        EditText new_pass;

        @BindView(R.id.re_pass)
        EditText re_pass;

        @BindView(R.id.change_pass_btn)
        Button change_pass_btn;

        @BindView(R.id.back_btn)
        ImageView back_btn;

        RetrofitApis retrofitApis;
        Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        retrofitApis=RetrofitApis.Factory.create(this);
        change_pass_btn.setOnClickListener(this);
        dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(ChangePasswordActivity.this);
        sessionManagement=new SessionManagement(ChangePasswordActivity.this);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private boolean validate() {
        String oldpass,newpass,repass;
        oldpass=old_pass.getText().toString();
        newpass=new_pass.getText().toString();
        repass=re_pass.getText().toString();

        if(oldpass==null||oldpass.isEmpty()){
            old_pass.setError("please enter old password");
            old_pass.requestFocus();
            return false;
        }
        if(oldpass.length()<6||oldpass.length()>12){
            old_pass.setError("password must 6 to 12 characters");
            old_pass.requestFocus();
            return false;
        }

        if(newpass==null||newpass.isEmpty()){
            new_pass.setError("please enter new password");
            new_pass.requestFocus();
            return false;
        }
        if(newpass.length()<6||newpass.length()>12){
            new_pass.setError("password must 6 to 12 characters");
            new_pass.requestFocus();
            return false;
        }
        if(repass==null||repass.isEmpty()){
            re_pass.setError("please enter re-enter password");
            re_pass.requestFocus();
            return false;
        }

        if(!newpass.equals(repass)){
            re_pass.setError("password and re-enter password must be same");
            re_pass.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {

        if(validate())
         {
             String old=old_pass.getText().toString().trim();
             String newpass=re_pass.getText().toString().trim();
             if(connectionDetector.isConnectingToInternet())
             {    if(sessionManagement.getUserType()==SessionManagement.AS_USER)
                   calltoserver(old,newpass,sessionManagement.getValueFromPreference(SessionManagement.USERID));
                 else if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT)
                    callToMerchantServer(old,newpass,sessionManagement.getValueFromPreference(SessionManagement.USERID));
             }
             else
                 callToast("You've no internet connection. Please try again.");
        }
    }
    public void calltoserver(String old,String newpass,String userid){
       Call<LoginModel> call= retrofitApis.changePassword(old,newpass,userid);
       dialog.show();
       call.enqueue(new Callback<LoginModel>() {
           @Override
           public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
               if(dialog!=null)
                   dialog.dismiss();
               if(response.isSuccessful()){
                   LoginModel model=response.body();
                   Toast.makeText(ChangePasswordActivity.this,model.getResult(),Toast.LENGTH_SHORT).show();
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
    public void callToMerchantServer(String old,String newpass,String userid){
        RetrofitMerchantApis merchantApis=RetrofitMerchantApis.Factory.create(this);
        Call<MLogin> call= merchantApis.changePassword(old,newpass,userid);
        dialog.show();
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if(response.isSuccessful()){
                    MLogin model=response.body();
                    Toast.makeText(ChangePasswordActivity.this,model.getResult(),Toast.LENGTH_SHORT).show();
                    if(model.getStatus()==1)
                        finish();
                }else if(response.code()==400){
                    Gson gson=new Gson();
                    MLogin model= null;
                    try {
                        model = gson.fromJson(response.errorBody().string(),MLogin.class);
                        if(model==null)return;
                        Toast.makeText(ChangePasswordActivity.this,model.getResult(),Toast.LENGTH_SHORT).show();
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



