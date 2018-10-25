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
import com.m.hyderabadmarketingapp.user_module.adapters.FeedsAdapter;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.user_module.models.Feed;
import com.m.hyderabadmarketingapp.user_module.models.FeedsListResponce;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feed_fragment extends Fragment implements View.OnClickListener,FeedsAdapter.ViewFullDetailsListener {

    @BindView(R.id.recyclerview_feedslist)
    RecyclerView recyclerview_feedslist;
    @BindView(R.id.feed_details_view)
    RelativeLayout feeddetails;

    @BindView(R.id.feed_image)
    ImageView feed_image_large;

    @BindView(R.id.feed_title)
    TextView feed_title;

    @BindView(R.id.feed_by_merchant)
    TextView feed_by_merchant;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.title_view)
    TextView titleView;
    FeedsAdapter adapter;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    RetrofitApis retrofitApis;
    private Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this,view);
        init(view);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        retrofitApis=  RetrofitApis.Factory.create(context);

    }
    private void init(View view)
    {
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getContext());
        sessionManagement=new SessionManagement(getContext());
        back_btn.setOnClickListener(this);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(connectionDetector.isConnectingToInternet())
            callService();
        else
            callToast("You've no internet connection. Please try again.");



    }
    private void callToast(String msg)
    {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
    private void callService() {
        Call<FeedsListResponce> call=retrofitApis.getFeeds();
        dialog.show();
        call.enqueue(new Callback<FeedsListResponce>() {
            @Override
            public void onResponse(Call<FeedsListResponce> call, Response<FeedsListResponce> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if (response.isSuccessful()){
                    recyclerview_feedslist.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter=new FeedsAdapter(getContext(),response.body().getFeed());
                    adapter.setListener(Feed_fragment.this);
                    recyclerview_feedslist.setAdapter(adapter );
                    if(response.body().getFeed()!=null && response.body().getFeed().size()>0)
                        tv_nodata.setVisibility(View.GONE);
                    else
                        tv_nodata.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<FeedsListResponce> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
    }
    public void visibleFeedDetails(int visiblility){
        feeddetails.setVisibility(visiblility);
        back_btn.setVisibility(visiblility);
        titleView.setText("feed Details");
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back_btn){
            visibleFeedDetails(View.GONE);
            recyclerview_feedslist.setVisibility(View.VISIBLE);
            titleView.setText("Feeds");
        }

    }

    @Override
    public void onViewFullDetails(Feed feed) {
        if(feed!=null){
            Picasso.with(getContext()).load(ApiUrls.FEED_IMAGEPATH+feed.getPhoto()).placeholder(R.mipmap.profile_img)
                    .error(R.mipmap.profile_img)
                    .into(feed_image_large);
            recyclerview_feedslist.setVisibility(View.GONE);
            visibleFeedDetails(View.VISIBLE);
            feed_by_merchant.setText("by "+feed.getMerchantName());
            description.setText(feed.getDescription());
            feed_title.setText(feed.getTitle());
        }

    }
}
