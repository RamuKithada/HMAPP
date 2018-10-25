package com.m.hyderabadmarketingapp.user_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.user_module.adapters.OffersAdapter;
import com.m.hyderabadmarketingapp.user_module.models.Offer;
import com.m.hyderabadmarketingapp.user_module.models.OffersListResponce;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarchantOffersActivity extends AppCompatActivity implements OffersAdapter.ViewOffersDetailsListerner {

    @BindView(R.id.recyclerview_offerslist)
    RecyclerView recyclerview_offerslist;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    RetrofitApis retrofitApis;
    private Dialog dialog;
    OffersAdapter adapter;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    private String merchantid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marchant_offers);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        dialog = new Dialog(MarchantOffersActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getApplicationContext());
        sessionManagement=new SessionManagement(getApplicationContext());

        retrofitApis=  RetrofitApis.Factory.create(getApplicationContext());
        tv_title.setText(getIntent().getStringExtra("merchant_name")+" Offers");
        merchantid=getIntent().getStringExtra("merchant_id");
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(connectionDetector.isConnectingToInternet())
            callService();
        else
            callToast("You've no internet connection. Please try again.");
    }

    private void callService() {
        Call<OffersListResponce> call=retrofitApis.merchantOffers(merchantid);
        dialog.show();
        call.enqueue(new Callback<OffersListResponce>() {
            @Override
            public void onResponse(Call<OffersListResponce> call, Response<OffersListResponce> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if (response.isSuccessful()){
                    OffersListResponce listResponce= response.body();
                    recyclerview_offerslist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter=new OffersAdapter(MarchantOffersActivity.this,response.body().getOffers());
                    recyclerview_offerslist.setAdapter(adapter);
                    adapter.setListerner(MarchantOffersActivity.this);
                    if(response.body().getOffers()!=null && response.body().getOffers().size()>0)
                        tv_nodata.setVisibility(View.GONE);
                    else
                        tv_nodata.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<OffersListResponce> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
    }

    private void callToast(String msg)
    {
        Toast.makeText(MarchantOffersActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOffersClick(Offer offer)
    {
        Intent intent=new Intent(MarchantOffersActivity.this,OfferDetailsActivity.class);
        intent.putExtra("offer",offer);
        startActivity(intent);
    }
}
