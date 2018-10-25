package com.m.hyderabadmarketingapp.user_module.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.activities.StoreDetailsScreen;
import com.m.hyderabadmarketingapp.user_module.fragments.Aroundme_fragment;
import com.m.hyderabadmarketingapp.user_module.models.CategoryStoreModel;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by admin on 4/20/2017.
 */

public class CategoriesStoreAdapter extends RecyclerView.Adapter<CategoriesStoreAdapter.MyViewHolder>
{
    private Activity context;
    private List<CategoryStoreModel.Categorystore> categorystores;
    private Aroundme_fragment aroundme_fragment;

    public CategoriesStoreAdapter(Aroundme_fragment aroundme_fragment, Activity context, List<CategoryStoreModel.Categorystore> categorystores)
    {
        this.context=context;
        this.categorystores=categorystores;
        this.aroundme_fragment=aroundme_fragment;
    }

    @Override
    public CategoriesStoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_storemodel, parent, false);

        return new CategoriesStoreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoriesStoreAdapter.MyViewHolder holder, final int position)
    {
        final CategoriesStoreAdapter.MyViewHolder myViewHolder=holder;
        final CategoryStoreModel.Categorystore model=categorystores.get(position);
        holder.tv_distance.setText(model.getDistance()+" KMS");
        holder.tv_marchantname.setText(model.getMerchantName());
        holder.tv_state.setText(model.getState());
        if(model.getRating()!=null)
           holder.tv_rating.setText(model.getRating());
        else
            holder.tv_rating.setText("0.0");

        Picasso.with(context).load(ApiUrls.MERCHANTBANNERS+model.getMerchantBanner()).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(holder.iv_storebanner);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, StoreDetailsScreen.class);
                intent.putExtra("merchantid",model.getMerchantId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return categorystores.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_marchantname,tv_rating,tv_state,tv_distance;
        public ImageView iv_storebanner;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_marchantname=(TextView) itemView.findViewById(R.id.tv_marchantname);
            tv_rating=(TextView) itemView.findViewById(R.id.tv_rating);
            tv_state=(TextView) itemView.findViewById(R.id.tv_state);
            tv_distance=(TextView) itemView.findViewById(R.id.tv_distance);
            iv_storebanner=(ImageView) itemView.findViewById(R.id.iv_storebanner);
        }
    }
}
