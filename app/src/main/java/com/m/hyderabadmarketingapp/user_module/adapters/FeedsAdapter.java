package com.m.hyderabadmarketingapp.user_module.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.models.Feed;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedsAdapter extends RecyclerView.Adapter <FeedsAdapter.MyViewHolder>{
    List<Feed> feeds;
    Context context;
    ViewFullDetailsListener listener;

    public FeedsAdapter(Context context, List<Feed> feeds) {
        this.feeds = feeds;
        this.context = context;
    }

    public void setListener(ViewFullDetailsListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        Feed feed = feeds.get(pos);
        Picasso.with(context).load(ApiUrls.FEED_IMAGEPATH+feed.getPhoto()).placeholder(R.mipmap.profile_img)
                .error(R.mipmap.profile_img)
                .into(holder.image);
        holder.feedName.setText(feed.getTitle());
        holder.feedbym.setText("by " +feed.getMerchantName());
        holder.des.setText(feed.getDescription());
        holder.viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onViewFullDetails(feeds.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (feeds ==null)
        return 0;

        return feeds.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.feed_name)
        TextView feedName;
        @BindView(R.id.feed_image)
        ImageView image;
        @BindView(R.id.feed_by_merchant)
        TextView feedbym;
        @BindView(R.id.viewall)
        TextView viewall;
        @BindView(R.id.description)
        TextView des;




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
    public interface ViewFullDetailsListener {
        void onViewFullDetails(Feed feed);
    }
}
