package com.m.hyderabadmarketingapp.user_module.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.activities.StoreDetailsScreen;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by admin on 4/20/2017.
 */

public class MarchantPhotosAdapter extends RecyclerView.Adapter<MarchantPhotosAdapter.MyViewHolder>
{
    private Activity context;
    private List<String> storeDetails;
    private int screenWidth,screenHeight;

    public MarchantPhotosAdapter(Activity context, List<String> storeDetails)
    {
        this.context=context;
        this.storeDetails=storeDetails;
        DisplayMetrics metrics=context.getResources().getDisplayMetrics();
        screenHeight=metrics.heightPixels;
        screenWidth=metrics.widthPixels;
    }

    @Override
    public MarchantPhotosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marchantphoto_model, parent, false);

        return new MarchantPhotosAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MarchantPhotosAdapter.MyViewHolder holder, final int position)
    {
        final MarchantPhotosAdapter.MyViewHolder myViewHolder=holder;
        String model=storeDetails.get(position);

        Picasso.with(context).load(ApiUrls.Merchant_Photos+model).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(holder.iv_merchantphoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(context instanceof StoreDetailsScreen)
                    ((StoreDetailsScreen) context).displayPhotoviewerDialog(position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return storeDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView iv_merchantphoto;
        public RelativeLayout layout_photogrid;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            iv_merchantphoto=(ImageView) itemView.findViewById(R.id.iv_merchantphoto);
            layout_photogrid=(RelativeLayout)itemView.findViewById(R.id.layout_photogrid);
            iv_merchantphoto.getLayoutParams().height=screenWidth/4;
            iv_merchantphoto.getLayoutParams().width=screenWidth/4;
        }
    }

}
