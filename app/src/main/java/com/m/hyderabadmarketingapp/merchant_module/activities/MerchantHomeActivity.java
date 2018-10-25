package com.m.hyderabadmarketingapp.merchant_module.activities;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.merchant_module.fragments.Loanoffers_fragment;
import com.m.hyderabadmarketingapp.merchant_module.fragments.Offers_fragment;
import com.m.hyderabadmarketingapp.merchant_module.fragments.Profile_fragment;
import com.m.hyderabadmarketingapp.merchant_module.fragments.Requirement_fragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantHomeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.layout_requirements)
    LinearLayout layout_requirements;
    @BindView(R.id.layout_loan_offers)
    LinearLayout layout_loan_offers;
    @BindView(R.id.layout_offers)
    LinearLayout layout_offers;
    @BindView(R.id.layout_profile)
    LinearLayout layout_profile;

    @BindView(R.id.iv_requirements)
    ImageView iv_requirements;
    @BindView(R.id.iv_loan_offers)
    ImageView iv_loan_offers;

    @BindView(R.id.iv_offers)
    ImageView iv_offers;
    @BindView(R.id.iv_profile)
    ImageView iv_profile;

    @BindView(R.id.tv_requirements)
    TextView tv_requirements;
    @BindView(R.id.tv_loan_offers)
    TextView tv_loan_offers;
    @BindView(R.id.tv_offers)
    TextView tv_offers;
    @BindView(R.id.tv_profile)
    TextView tv_profile;
    SessionManagement sessionManagement;
    private FragmentTransaction ft;
    private FragmentManager fragmentManager;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_home);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        layout_requirements.setOnClickListener(this);
        layout_loan_offers.setOnClickListener(this);
        layout_offers.setOnClickListener(this);
        layout_profile.setOnClickListener(this);
        sessionManagement=new SessionManagement(this);

        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        fragment=new Requirement_fragment();
        ft.add(R.id.layout_framefor_fragment,fragment);
        ft.commit();
        setClickedFocus(layout_requirements);
    }
    private void setClickedFocus(View  action)
    {
        layout_requirements.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_loan_offers.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_offers.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_profile.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));

        iv_requirements.setImageResource(R.mipmap.requirements_black);
        iv_loan_offers.setImageResource(R.mipmap.loan_offers_black);
        iv_offers.setImageResource(R.mipmap.offer_black);
        iv_profile.setImageResource(R.mipmap.profile_black);

        tv_requirements.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_loan_offers.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_offers.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_profile.setTextColor(getResources().getColor(R.color.textcolorblack));
        switch (action.getId()){
            case R.id.layout_requirements:
                layout_requirements.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_requirements.setImageResource(R.mipmap.requirements);
                tv_requirements.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.layout_loan_offers:
                layout_loan_offers.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_loan_offers.setImageResource(R.mipmap.loan_offers_white);
                tv_loan_offers.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.layout_offers:
                layout_offers.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_offers.setImageResource(R.mipmap.offer_white);
                tv_offers.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.layout_profile:
                layout_profile.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_profile.setImageResource(R.mipmap.profile_white);
                tv_profile.setTextColor(Color.parseColor("#ffffff"));
                sessionManagement.logoutUser();
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_requirements:
                changeFragment(new Requirement_fragment());
                break;
            case R.id.layout_loan_offers:
                changeFragment(new Loanoffers_fragment());
                break;
            case R.id.layout_offers:
                changeFragment(new Offers_fragment());
                break;
            case R.id.layout_profile:
                changeFragment(new Profile_fragment());
                break;
        }
        setClickedFocus(view);
    }

    private void changeFragment(Fragment fragment)
    {
        this.fragment=fragment;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.layout_framefor_fragment, fragment);
        ft.commit();
        invalidateOptionsMenu();
    }
}
