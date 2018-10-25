package com.m.hyderabadmarketingapp.user_module.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.models.Offer;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfferDetailsActivity extends AppCompatActivity {

    private Offer offer;
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

    @BindView(R.id.back_btn)
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        offer = (Offer) getIntent().getParcelableExtra("offer");
        offer_address.setText(offer.getAddress());
        offer_title.setText(offer.getOfferName());
        offer_org_price.setText("Orgilan price : "+getString(R.string.rs)+" "+offer.getPrice());
        offer_price.setText(getString(R.string.rs)+" "+offer.getOfferPrice());
        offer_percent.setText(offer.getDiscount()+" % "+"off");
        offer_validity.setText("offer valid from "+offer.getValidFrom()+" to "+offer.getValidTo());

        Picasso.with(getApplicationContext()).load(ApiUrls.OFFERS_IMAGEPATH+offer.getImage()).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(offer_image);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
