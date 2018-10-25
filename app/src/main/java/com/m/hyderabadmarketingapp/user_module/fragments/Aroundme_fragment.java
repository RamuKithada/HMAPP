package com.m.hyderabadmarketingapp.user_module.fragments;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.adapters.CategoriesAdapter;
import com.m.hyderabadmarketingapp.user_module.adapters.CategoriesStoreAdapter;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.user_module.models.CategoryModel;
import com.m.hyderabadmarketingapp.user_module.models.CategoryStoreModel;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Aroundme_fragment extends Fragment implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    @BindView(R.id.recyclerview_categories)
    RecyclerView recyclerview_categories;
    @BindView(R.id.recyclerview_categorieslist)
    RecyclerView recyclerview_categorieslist;
    @BindView(R.id.layout_titlecontainer)
    RelativeLayout layout_titlecontainer;
    @BindView(R.id.search_layout)
    RelativeLayout search_layout;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private CategoriesAdapter categoriesAdapter;
    private CategoriesStoreAdapter categoriesStoreAdapter;
    private List<CategoryModel.Category> categorieslist;
    private List<CategoryStoreModel.Categorystore> categorystoreslist;
    private List<CategoryStoreModel.Categorystore> categorystoresfilterlist;
    private LinearLayoutManager layoutManager;
    private Aroundme_fragment aroundme_fragment;
    private String fiterString = "";
    //private String lattitude="17.3933",longnitude="75.5334";
    private String lattitude,longnitude;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view = inflater.inflate(R.layout.fragment_aroundme, container, false);
        ButterKnife.bind(this, view);
        init(view);
        return view;
    }

    private void init(View view) {

        aroundme_fragment = this;
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector = new ConnectionDetector(getActivity());

        categorieslist = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview_categories.setLayoutManager(layoutManager);
        categoriesAdapter = new CategoriesAdapter(aroundme_fragment, getActivity(), categorieslist);
        recyclerview_categories.setAdapter(categoriesAdapter);

        categorystoreslist = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(false);
        recyclerview_categorieslist.setLayoutManager(layoutManager);
        categoriesStoreAdapter = new CategoriesStoreAdapter(aroundme_fragment, getActivity(), categorystoreslist);
        recyclerview_categorieslist.setAdapter(categoriesStoreAdapter);

        iv_search.setOnClickListener(this);
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                fiterString = s.toString();
                filter(fiterString);
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if(!checkingPermissionAreEnabledOrNot())
            requestMultiplePermission();
            //displayCategoriesAndStores();
    }

    private void displayCategoriesAndStores() {
        if (connectionDetector.isConnectingToInternet())
            categoryService();
        else
            callToast("You've no internet connection. Please try again.");
    }

    private void categoryService() {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<CategoryModel> call = service.categoriesService();
        call.enqueue(new Callback<CategoryModel>() {
            @Override
            public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                dialog.dismiss();
                categorieslist.clear();
                CategoryModel model = response.body();
                if (model != null) {
                    Log.e("categorieslist size", "size" + model.getCategories().size());
                    if (model.getCategories() != null) {
                        categorieslist.addAll(model.getCategories());
                    }
                }
                categoriesAdapter.notifyDataSetChanged();
                getClickedCategory(categorieslist.get(0).getId());
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    public void getClickedCategory(String type_of_store) {
        layout_titlecontainer.setVisibility(View.VISIBLE);
        search_layout.setVisibility(View.GONE);

            if (connectionDetector.isConnectingToInternet()) {
                categoryStoreService(type_of_store, lattitude, longnitude);
            } else {
                callToast("You've no internet connection. Please try again.");
            }
    }

   private void categoryStoreService(String type_of_store, String latitude, String longitude) {
        Log.e("url around_me",""+ApiUrls.BASEURL+"categorystores");
        Log.e("categorystores params","type_of_store="+type_of_store+" latitude="+latitude+" longitude="+longitude);
        dialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<CategoryStoreModel> call = service.categorystoresService(type_of_store, latitude, longitude);
        call.enqueue(new Callback<CategoryStoreModel>() {
            @Override
            public void onResponse(Call<CategoryStoreModel> call, Response<CategoryStoreModel> response) {
                dialog.dismiss();
                categorystoresfilterlist = new ArrayList<>();
                categorystoreslist.clear();
                categoriesStoreAdapter.notifyDataSetChanged();
                CategoryStoreModel model = response.body();
                if (model != null) {
                    if (model != null) {
                        if (model.getCategorystores() != null) {
                            categorystoreslist.addAll(model.getCategorystores());
                            categorystoresfilterlist.addAll(model.getCategorystores());
                        }
                    }
                }
                categoriesStoreAdapter.notifyDataSetChanged();
                if (categorystoreslist.size() > 0)
                    tv_nodata.setVisibility(View.GONE);
                else
                    tv_nodata.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<CategoryStoreModel> call, Throwable t) {
                dialog.dismiss();
                categorystoresfilterlist = new ArrayList<>();
                categorystoreslist.clear();
                categoriesStoreAdapter.notifyDataSetChanged();
                Log.e("failure",t.getMessage());
                callToast(t.getMessage());
            }
        });
    }

    private void callToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void filter(String text) {
        categorystoreslist.clear();
        for (int i = 0; i < categorystoresfilterlist.size(); i++) {
            if (categorystoresfilterlist.get(i).getMerchantName().toLowerCase().startsWith(text.toLowerCase()))
                categorystoreslist.add(categorystoresfilterlist.get(i));
        }
        categoriesStoreAdapter.notifyDataSetChanged();
        if (categorystoreslist.size() > 0)
            tv_nodata.setVisibility(View.GONE);
        else
            tv_nodata.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_search:
                setSearch_layout();
                break;
        }
    }

    public void setSearch_layout() {
        if (search_layout.getVisibility() == View.VISIBLE) {
            layout_titlecontainer.setVisibility(View.VISIBLE);
            search_layout.setVisibility(View.GONE);
            fiterString = "";
            et_search.setText(fiterString);
            filter(fiterString);
        } else {
            layout_titlecontainer.setVisibility(View.GONE);
            search_layout.setVisibility(View.VISIBLE);
            fiterString = "";
            et_search.setText(fiterString);
            filter(fiterString);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
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
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            callToast("Location not Detected");
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
            lattitude=Double.toString(location.getLatitude());
            longnitude=Double.toString(location.getLongitude());
            //callToast("updated "+lattitude+" "+longnitude);
            if (connectionDetector.isConnectingToInternet()) {
                displayCategoriesAndStores();
            } else {
                callToast("You've no internet connection. Please try again.");
            }
        }
    }

    public boolean checkingPermissionAreEnabledOrNot() {
        int coarseloc = ContextCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION);
        int accessloc = ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION);
        return coarseloc == PackageManager.PERMISSION_GRANTED && accessloc == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION
                }, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkingPermissionAreEnabledOrNot())
                    {
                        startLocationUpdates();
                    } else {
                        callToast("Location permission is mandatory to use this app.");
                        getActivity().finish();
                    }
                }
                return;
        }
    }
}
