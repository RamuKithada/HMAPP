package com.m.hyderabadmarketingapp.merchant_module.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m.hyderabadmarketingapp.R;


public class Requirement_fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_requirement_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {

    }
}
