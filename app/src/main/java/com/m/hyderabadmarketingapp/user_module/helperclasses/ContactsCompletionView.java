package com.m.hyderabadmarketingapp.user_module.helperclasses;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.models.TagsModel;
import com.tokenautocomplete.TokenCompleteTextView;

public class ContactsCompletionView extends TokenCompleteTextView<TagsModel.Tag> {
    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(TagsModel.Tag person) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        view.setText(person.getTagName());

        return view;
    }

    @Override
    protected TagsModel.Tag defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
       return null;
    }
}