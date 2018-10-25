package com.m.hyderabadmarketingapp.user_module.activities;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.VolleyServiceCall;
import com.m.hyderabadmarketingapp.helperclasses.ConnectionDetector;
import com.m.hyderabadmarketingapp.helperclasses.SessionManagement;
import com.m.hyderabadmarketingapp.user_module.helperclasses.ContactsCompletionView;
import com.m.hyderabadmarketingapp.user_module.models.CategoryModel;
import com.m.hyderabadmarketingapp.user_module.models.LoginModel;
import com.m.hyderabadmarketingapp.user_module.models.Person;
import com.m.hyderabadmarketingapp.user_module.models.TagsModel;
import com.m.hyderabadmarketingapp.user_module.presenter.ApiUrls;
import com.m.hyderabadmarketingapp.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;
import com.tokenautocomplete.TokenCompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadRequirementActivity extends AppCompatActivity implements View.OnClickListener,TokenCompleteTextView.TokenListener{

    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.iv_requiredimage)
    ImageView iv_requiredimage;
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_description)
    EditText et_description;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    @BindView(R.id.searchView)
    ContactsCompletionView completionView;

    private String image_path;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private SessionManagement sessionManagement;
    private List<TagsModel.Tag> tags=new ArrayList<TagsModel.Tag>();
    ArrayAdapter<TagsModel.Tag> adapter;
    private ArrayList<String> selectedTags=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_requirement);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        dialog = new Dialog(UploadRequirementActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);

        connectionDetector=new ConnectionDetector(getApplicationContext());
        sessionManagement=new SessionManagement(getApplicationContext());

        image_path=getIntent().getStringExtra("image_path");
        back_btn.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        Picasso.with(getApplicationContext()).load(new File(image_path)).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(iv_requiredimage);

        if(connectionDetector.isConnectingToInternet())
        {
            getTagslistService();
        }
        else {
            callToast("You've no internet connection. Please try again.");
        }

    }


    private void getTagslistService() {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<TagsModel> call = service.tagsListService();
        call.enqueue(new Callback<TagsModel>() {
            @Override
            public void onResponse(Call<TagsModel> call, Response<TagsModel> response) {
                dialog.dismiss();
                tags.clear();
                TagsModel model = response.body();
                if (model != null) {
                    Log.e("tagslist size", "size" + model.getTags().size());
                    if (model.getTags() != null) {
                        tags.addAll(model.getTags());
                        selectedTags=new ArrayList<>();
                        setAutoSearchTags();
                    }
                }
            }

            @Override
            public void onFailure(Call<TagsModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void setAutoSearchTags()
    {
        TagsModel.Tag[] tagsarray = tags.toArray(new TagsModel.Tag[tags.size()]);
        adapter = new ArrayAdapter<TagsModel.Tag>(this, android.R.layout.simple_list_item_1, tagsarray);
        completionView.setAdapter(adapter);
        completionView.setTokenListener(this);
        completionView.allowDuplicates(false);
    }

    @Override
    public void onTokenAdded(Object token) {
        TagsModel.Tag tag=(TagsModel.Tag)token;
        Log.e("person",""+tag.getTagId());
        selectedTags.add(tag.getTagId());
    }

    @Override
    public void onTokenRemoved(Object token) {
        TagsModel.Tag tag=(TagsModel.Tag)token;
        Log.e("person",""+tag.getTagId());
        selectedTags.remove(tag.getTagId());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back_btn:
                 finish();
                break;

            case R.id.btn_submit:
              checkinputValidations();
                break;
        }
    }

    private void callToast(String msg)
    {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void checkinputValidations()
    {
        String title,description,tags,image,userid;
        userid=sessionManagement.getValueFromPreference(SessionManagement.USERID);
        title=et_title.getText().toString().trim();
        description=et_description.getText().toString().trim();
        tags=selectedTags.toString();
        if(selectedTags.size()==0)
            tags="[]";
        image=image_path;

        if(title.length()==0)
            callToast("Please enter title");
        else if(title.length()<3)
            callToast("Please enter title name atleast 3 charecters");
        else if(description.length()==0)
            callToast("Please enter description");
        else if(description.length()<5)
            callToast("Please enter description atleast 5 charecters");
        else {
            if(connectionDetector.isConnectingToInternet())
            {
                uploadRequirement(userid,title,description,tags,image_path);
            }
            else {
                callToast("You've no internet connection. Please try again.");
            }
        }
    }

    public void uploadRequirement(String user_id,String title,String description,String tags,String path)
    {
        dialog.show();
        Bitmap bitmap= BitmapFactory.decodeFile(path);
        Map<String,String> params=new HashMap<>();
        params.put("user_id",user_id);
        params.put("title",title);
        params.put("description",description);
        params.put("tags",tags);
        VolleyServiceCall.uploadBitmap(UploadRequirementActivity.this, ApiUrls.BASEURL+"addrequirement", "image", bitmap, params, new VolleyServiceCall.ServiceResponse() {
            @Override
            public void getResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject!=null)
                    {
                        if(jsonObject.getInt("status")==1) {
                            callToast("Requirement uploaded successfully");
                            finish();
                        }
                        else
                            callToast(jsonObject.getString("result"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getErrorResponse(String errormessage) {
                Log.e("upload response"," failed ");
                dialog.dismiss();
                callToast(errormessage);
            }
        });

    }

}
