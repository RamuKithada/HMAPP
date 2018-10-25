package com.m.hyderabadmarketingapp.user_module.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.fragments.Aroundme_fragment;
import com.m.hyderabadmarketingapp.user_module.fragments.Feed_fragment;
import com.m.hyderabadmarketingapp.user_module.fragments.Offers_fragment;
import com.m.hyderabadmarketingapp.user_module.fragments.Profile_fragment;
import com.m.hyderabadmarketingapp.user_module.fragments.Upload_fragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.layout_aroundme)
    LinearLayout layout_aroundme;
    @BindView(R.id.layout_upload)
    LinearLayout layout_upload;
    @BindView(R.id.layout_feed)
    LinearLayout layout_feed;
    @BindView(R.id.layout_offers)
    LinearLayout layout_offers;
    @BindView(R.id.layout_profile)
    LinearLayout layout_profile;

    @BindView(R.id.iv_aroundme)
    ImageView iv_aroundme;
    @BindView(R.id.iv_upload)
    ImageView iv_upload;
    @BindView(R.id.iv_feed)
    ImageView iv_feed;
    @BindView(R.id.iv_offers)
    ImageView iv_offers;
    @BindView(R.id.iv_profile)
    ImageView iv_profile;

    @BindView(R.id.tv_aroundme)
    TextView tv_aroundme;
    @BindView(R.id.tv_upload)
    TextView tv_upload;
    @BindView(R.id.tv_feed)
    TextView tv_feed;
    @BindView(R.id.tv_offers)
    TextView tv_offers;
    @BindView(R.id.tv_profile)
    TextView tv_profile;

    private Dialog homescreenpopup;
    private FragmentTransaction ft;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    public String AROUNDME="around me",UPLOAD="upload",FEED="feed",OFFERS="offers",PROFILE="profile";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        handler=new Handler();
        displayCampaign();
        handler.postDelayed(th,4000);
        layout_aroundme.setOnClickListener(this);
        layout_upload.setOnClickListener(this);
        layout_feed.setOnClickListener(this);
        layout_offers.setOnClickListener(this);
        layout_profile.setOnClickListener(this);

        setClickedFocus(AROUNDME);
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        fragment=new Aroundme_fragment();
        ft.add(R.id.layout_framefor_fragment,fragment);
        ft.commit();
    }

    private void changeFragment(Fragment fragment)
    {
        this.fragment=fragment;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.layout_framefor_fragment, fragment);
        ft.commit();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        displayExitDialog();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.layout_aroundme:
                setClickedFocus(AROUNDME);
                changeFragment(new Aroundme_fragment());
                break;
            case R.id.layout_upload:
                setClickedFocus(UPLOAD);
                changeFragment(new Upload_fragment());
                break;
            case R.id.layout_feed:
                setClickedFocus(FEED);
                changeFragment(new Feed_fragment());
                break;
            case R.id.layout_offers:
                setClickedFocus(OFFERS);
                changeFragment(new Offers_fragment());
                break;
            case R.id.layout_profile:
                setClickedFocus(PROFILE);
                changeFragment(new Profile_fragment());
                break;
        }
    }

    private void setClickedFocus(String action)
    {
        layout_aroundme.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_upload.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_feed.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_offers.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_profile.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));

        iv_aroundme.setImageResource(R.mipmap.aroundme_black);
        iv_upload.setImageResource(R.mipmap.upload_black);
        iv_feed.setImageResource(R.mipmap.feed_black);
        iv_offers.setImageResource(R.mipmap.offer_black);
        iv_profile.setImageResource(R.mipmap.profile_black);

        tv_aroundme.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_upload.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_feed.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_offers.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_profile.setTextColor(getResources().getColor(R.color.textcolorblack));

        if(action.equalsIgnoreCase(AROUNDME))
        {
            layout_aroundme.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
            iv_aroundme.setImageResource(R.mipmap.aroundme_white);
            tv_aroundme.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(action.equalsIgnoreCase(UPLOAD))
        {
            layout_upload.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
            iv_upload.setImageResource(R.mipmap.upload_white);
            tv_upload.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(action.equalsIgnoreCase(FEED))
        {
            layout_feed.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
            iv_feed.setImageResource(R.mipmap.feed_white);
            tv_feed.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(action.equalsIgnoreCase(OFFERS))
        {
            layout_offers.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
            iv_offers.setImageResource(R.mipmap.offer_white);
            tv_offers.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(action.equalsIgnoreCase(PROFILE))
        {
            layout_profile.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
            iv_profile.setImageResource(R.mipmap.profile_white);
            tv_profile.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0) {

                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){

                    }
                }
        }
    }

    private void displayExitDialog()
    {
        final Dialog dialog=new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_gallery_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        RelativeLayout layout_yes=(RelativeLayout) dialog.findViewById(R.id.layout_yes);
        RelativeLayout layout_no=(RelativeLayout)dialog.findViewById(R.id.layout_no);
        TextView tv_title=(TextView)dialog.findViewById(R.id.tv_title);
        tv_title.setText("Are you sure you want to exit from app?");
        TextView tv_no=(TextView)dialog.findViewById(R.id.tv_no);
        tv_no.setText("No");
        TextView tv_yes=(TextView)dialog.findViewById(R.id.tv_yes);
        tv_yes.setText("Yes");

        layout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                finish();
                finishAffinity();
            }
        });

        layout_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void displayCampaign()
    {
        DisplayMetrics metrics=this.getResources().getDisplayMetrics();
        int screenWidth=metrics.widthPixels;
        int screenHeight=metrics.heightPixels;

        homescreenpopup=new Dialog(HomeActivity.this);
        homescreenpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        homescreenpopup.setContentView(R.layout.homescreen_popup);
        homescreenpopup.setCanceledOnTouchOutside(true);
        homescreenpopup.setCancelable(false);

        ImageView iv_close=(ImageView) homescreenpopup.findViewById(R.id.iv_close);
        ImageView iv_popup_image=(ImageView)homescreenpopup.findViewById(R.id.iv_popup_image);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homescreenpopup.dismiss();
                handler.removeCallbacks(th);
            }
        });

        homescreenpopup.show();
        homescreenpopup.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, (screenHeight/4)*3);
    }

    Runnable th=new Runnable() {
        @Override
        public void run() {

            if(homescreenpopup!=null)
            {
                if(homescreenpopup.isShowing())
                    homescreenpopup.dismiss();
                handler.removeCallbacks(th);
            }
        }
    };
}
