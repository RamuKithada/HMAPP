package com.m.hyderabadmarketingapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.GPSTracker;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.merchant_module.models.MLogin;
import com.m.hyderabadmarketingapp.merchant_module.presenter.MerchantApiUrls;
import com.m.hyderabadmarketingapp.merchant_module.presenter.RetrofitMerchantApis;
import com.m.hyderabadmarketingapp.user_module.activities.OtpScreen;
import com.m.hyderabadmarketingapp.user_module.activities.StoreDetailsScreen;
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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECEIVE_SMS;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    @BindView(R.id.layout_signup)
    RelativeLayout layout_signup;
    @BindView(R.id.tv_alreadyhave_account)
    TextView tv_alreadyhave_account;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_mobilenumber)
    EditText et_mobilenumber;
    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.user)
    TextView loginAsUser;

    @BindView(R.id.merchant)
    TextView loginAsMerchant;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private SessionManagement sessionManagement;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private String lattitude="0.0",longnitude="0.0";

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
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        tv_alreadyhave_account.setOnClickListener(this);
        layout_signup.setOnClickListener(this);
        loginAsUser.setOnClickListener(loginSelecter);
        loginAsMerchant.setOnClickListener(loginSelecter);
        dialog = new Dialog(SignupActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(SignupActivity.this);
        sessionManagement=new SessionManagement(SignupActivity.this);

        if(!CheckingPermissionIsEnabledOrNot())
            RequestMultiplePermission(false);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tv_alreadyhave_account:
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.layout_signup:
                if(sessionManagement.getUserType()>0)
                    checkSignupValidations(et_name.getText().toString().trim(),et_mobilenumber.getText().toString().trim(),et_password.getText().toString().trim());
                 else
                    callToast("Please select User or Merchant");
                break;
        }
    }

    private void callToast(String msg)
    {
        Toast.makeText(SignupActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void checkSignupValidations(String name,String mobile_number,String password)
    {
      if(name.trim().length()==0)
          callToast("Please enter your name");
      else if(name.trim().length()<3)
          callToast("Please enter atleast 3 digits of your name");
      else if(mobile_number.trim().length()==0)
          callToast("Please enter your mobile number");
      else if(mobile_number.trim().length()<4)
          callToast("Please enter valid mobile number");
      else if(password.trim().length()==0)
          callToast("Please enter password");
      else if(password.trim().length()<6)
          callToast("Please enter atleast 6 digits password");
      else {
           if(connectionDetector.isConnectingToInternet())
           {
               if(sessionManagement.getUserType()==SessionManagement.AS_USER)
                        signupService(name,mobile_number,password,"android","123","direct");
               else if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT) {
                   if(CheckingLocationIsEnabledOrNot()) {
                       signupAsMerchant(name, mobile_number, password, "android", "123", "direct", lattitude, longnitude);
                   }
                   else {
                       callToast("Please allow Location Permissions to use this app");
                       RequestMultiplePermission(true);
                   }
               }
           }
           else
               callToast("You've no internet connection. Please try again.");
      }
    }
    private void signupService(String name, final String mobile_number, String password, String device_type, String device_token, String logintype)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.signupService(name,mobile_number,password,device_type,device_token,logintype);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    if(model.getStatus().equalsIgnoreCase("1"))
                    {
                        Intent otpintent=new Intent(SignupActivity.this,OtpScreen.class);
                        otpintent.putExtra("mobilenumber",mobile_number);
                        startActivity(otpintent);
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
    private void signupAsMerchant(String name, final String mobile_number, String password, String device_type, String device_token, String logintype,String latitude, String longitude)
    {
        dialog.show();
        RetrofitMerchantApis service = RetrofitMerchantApis.Factory.create(this);
        Call<MLogin> call = service.signupService(logintype,name,mobile_number,password,device_type,device_token,latitude,longitude);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1)
                    {
                        Intent otpintent = new Intent(SignupActivity.this, OtpScreen.class);
                        otpintent.putExtra("mobilenumber", mobile_number);
                        startActivity(otpintent);
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
                Log.e("get response","onFailure");
            }
        });

    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int readsms = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int coarseloc = ContextCompat.checkSelfPermission(SignupActivity.this, ACCESS_COARSE_LOCATION);
        int accessloc = ContextCompat.checkSelfPermission(SignupActivity.this, ACCESS_FINE_LOCATION);
        return readsms == PackageManager.PERMISSION_GRANTED && coarseloc==PackageManager.PERMISSION_GRANTED && accessloc==PackageManager.PERMISSION_GRANTED;
    }

    public boolean CheckingLocationIsEnabledOrNot() {
        int coarseloc = ContextCompat.checkSelfPermission(SignupActivity.this, ACCESS_COARSE_LOCATION);
        int accessloc = ContextCompat.checkSelfPermission(SignupActivity.this, ACCESS_FINE_LOCATION);
        return coarseloc==PackageManager.PERMISSION_GRANTED && accessloc==PackageManager.PERMISSION_GRANTED;
    }


    private void RequestMultiplePermission(boolean isOnlyLocation) {

        // Creating String Array with Permissions.
        if(isOnlyLocation)
        {
            ActivityCompat.requestPermissions(SignupActivity.this, new String[]
                    {
                            ACCESS_COARSE_LOCATION,
                            ACCESS_FINE_LOCATION
                    }, 100);
        }
        else {
            ActivityCompat.requestPermissions(SignupActivity.this, new String[]
                    {
                            RECEIVE_SMS,
                            ACCESS_COARSE_LOCATION,
                            ACCESS_FINE_LOCATION
                    }, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 100:

                if (grantResults.length > 0) {

                    if (CheckingLocationIsEnabledOrNot())
                    {
                        startLocationUpdates();
                    }

                    boolean readsms_permission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readsms_permission) {
                       callToast("Auto Read OTP Permission Granted");
                    }
                }

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            //lattitude=String.valueOf(mLocation.getLatitude());
            //longnitude=String.valueOf(mLocation.getLongitude());
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("suspended", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("failed", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location==null)
        {
            callToast("Location not detected");
            return;
        }
        if(lattitude==null && longnitude==null)
        {
            lattitude = Double.toString(location.getLatitude());
            longnitude = Double.toString(location.getLongitude());
            Log.e("lat longs","lat="+lattitude+" long="+longnitude);
        }
    }
}
