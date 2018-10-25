package com.m.hyderabadmarketingapp.user_module.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.activities.UploadRequirementActivity;
import com.theartofdev.edmodo.cropper.CropImage;


import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class Upload_fragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.btn_ok)
    Button btn_ok;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_upload_fragment, container, false);
        ButterKnife.bind(this,view);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        init(view);
        return view;
    }

    private void init(View view)
    {
       btn_ok.setOnClickListener(this);
    }

    private void callToast(String msg)
    {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_ok:
                if(!checkingPermissionAreEnabledOrNot())
                    requestMultiplePermission();
                else
                    picGalleryImage();
                break;
        }
    }

    public boolean checkingPermissionAreEnabledOrNot() {
        int camera = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
        int write = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED && write==PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, 100);

    }

    private void picGalleryImage()
    {
        CropImage.activity()
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String path=resultUri.getPath();
                Log.e("path is","path "+path);
                Intent intent=new Intent(getActivity(),UploadRequirementActivity.class);
                intent.putExtra("image_path",path);
                startActivity(intent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
