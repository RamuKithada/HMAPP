package com.m.hyderabadmarketingapp.user_module.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.fragments.Aroundme_fragment;
import com.m.hyderabadmarketingapp.user_module.models.CategoryModel;

import java.util.List;


/**
 * Created by admin on 4/20/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>
{
    private Activity context;
    private List<CategoryModel.Category> categories;
    private int clickedpos=0;
    private Aroundme_fragment aroundme_fragment;

    public CategoriesAdapter(Aroundme_fragment aroundme_fragment,Activity context, List<CategoryModel.Category> categories)
    {
        this.context=context;
        this.categories=categories;
        this.aroundme_fragment=aroundme_fragment;
    }

    @Override
    public CategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_model, parent, false);

        return new CategoriesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoriesAdapter.MyViewHolder holder, final int position)
    {
        final CategoriesAdapter.MyViewHolder myViewHolder=holder;
        CategoryModel.Category model=categories.get(position);
        holder.tv_category_name.setText(model.getName());
        if(clickedpos==position)
        {
            holder.layout_category.setBackgroundColor(context.getResources().getColor(R.color.colorOrangebutton));
            holder.tv_category_name.setTextColor(Color.parseColor("#ffffff"));
        }
        else {
            holder.layout_category.setBackgroundColor(context.getResources().getColor(R.color.colorHomeCategoriesbg));
            holder.tv_category_name.setTextColor(context.getResources().getColor(R.color.textcolorblack));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedpos=position;
                notifyDataSetChanged();
                aroundme_fragment.getClickedCategory(categories.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return categories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_category_name;
        public RelativeLayout layout_category;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_category_name=(TextView) itemView.findViewById(R.id.tv_category_name);
            layout_category=(RelativeLayout)itemView.findViewById(R.id.layout_category);
        }
    }

}
