package com.m.hyderabadmarketingapp.user_module.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.adapters.OffersAdapter;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.user_module.models.Offer;
import com.m.hyderabadmarketingapp.user_module.models.OffersListResponce;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Offers_fragment extends Fragment implements View.OnClickListener,OffersAdapter.ViewOffersDetailsListerner {

    @BindView(R.id.recyclerview_offerslist)
    RecyclerView recyclerview_offerslist;

    @BindView(R.id.back_btn)
    ImageView backBtn;

    @BindView(R.id.title_view)
    TextView titleView;

    @BindView(R.id.offer_image)
    ImageView offer_image;

    @BindView(R.id.offer_title)
    TextView offer_title;

    @BindView(R.id.offer_address)
            TextView offer_address;

    @BindView(R.id.offer_org_price)
    TextView offer_org_price;

    @BindView(R.id.offer_price)
    TextView offer_price;

    @BindView(R.id.offer_percent)
    TextView offer_percent;

    @BindView(R.id.offer_validity)
    TextView offer_validity;

    @BindView(R.id.offers_details_view)
    RelativeLayout offers_details_view;

    @BindView(R.id.tv_nodata)
     TextView tv_nodata;

    RetrofitApis retrofitApis;
    private Dialog dialog;
    OffersAdapter adapter;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      retrofitApis=  RetrofitApis.Factory.create(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_offers, container, false);
        ButterKnife.bind(this,view);
        backBtn.setOnClickListener(this);
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getContext());
        sessionManagement=new SessionManagement(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(connectionDetector.isConnectingToInternet())
        callService();
        else
        callToast("You've no internet connection. Please try again.");


    }

    private void callService() {
        Call<OffersListResponce> call=retrofitApis.getOffers();
        dialog.show();
        call.enqueue(new Callback<OffersListResponce>() {
            @Override
            public void onResponse(Call<OffersListResponce> call, Response<OffersListResponce> response) {
           if(dialog!=null)
               dialog.dismiss();
           if (response.isSuccessful()){
              OffersListResponce listResponce= response.body();
           recyclerview_offerslist.setLayoutManager(new LinearLayoutManager(getContext()));
           adapter=new OffersAdapter(getContext(),response.body().getOffers());
           recyclerview_offerslist.setAdapter(adapter);
           adapter.setListerner(Offers_fragment.this);
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
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        visibleOfferDetails(View.GONE);
        recyclerview_offerslist.setVisibility(View.VISIBLE);
        titleView.setText("offers");
    }

    @Override
    public void onOffersClick(Offer offer) {
        Picasso.with(getContext()).load(ApiUrls.OFFERS_IMAGEPATH+offer.getImage()).placeholder(R.mipmap.profile_img)
                .error(R.mipmap.profile_img)
                .into(offer_image);
        titleView.setText("offer Details");
        visibleOfferDetails(View.VISIBLE);
        recyclerview_offerslist.setVisibility(View.GONE);
        offer_address.setText(offer.getAddress());
        offer_title.setText(offer.getOfferName());
        offer_org_price.setText("Orgilan price : "+getContext().getString(R.string.rs)+" "+offer.getPrice());
        offer_price.setText(getContext().getString(R.string.rs)+" "+offer.getOfferPrice());
          offer_percent.setText(offer.getDiscount()+" % "+"off");
          offer_validity.setText("offer valid from "+offer.getValidFrom()+" to "+offer.getValidTo());
    }
    public void visibleOfferDetails(int visiblility){
        offers_details_view.setVisibility(visiblility);
        backBtn.setVisibility(visiblility);

    }
}
