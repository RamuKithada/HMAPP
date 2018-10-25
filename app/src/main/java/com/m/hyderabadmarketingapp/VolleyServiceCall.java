package com.m.hyderabadmarketingapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by madhu on 3/31/2018.
 */

public class VolleyServiceCall {

    public static int GET= Request.Method.GET;
    public static int POST= Request.Method.POST;
    public static void executeServiceCall(final Activity activity, int req_type, String url, final Map<String, String> header_params, final Map<String, String> params, final ServiceResponse serviceResponse)
    {
            StringRequest stringRequest = new StringRequest(req_type, url,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            serviceResponse.getResponse(response);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                            if(response != null && response.data != null){
                                switch(response.statusCode){
                                    case 401:

                                        String json = new String(response.data);
                                        serviceResponse.getResponse(json);

                                        break;
                                    default:
                                        serviceResponse.getErrorResponse(error.getMessage());
                                }
                            }
                        }
                    }) {
               @Override
                protected Map<String, String> getParams() {

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    return header_params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(stringRequest);
    }


    public static void uploadBitmap(final Context context, final String url, final String file_key, final Bitmap bitmap, final Map<String, String> params, final ServiceResponse serviceResponse)
    {
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String json = new String(response.data);
                        serviceResponse.getResponse(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serviceResponse.getErrorResponse(error.getMessage());
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put(file_key, new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(volleyMultipartRequest);
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public interface ServiceResponse {

        void getResponse(String response);
        void getErrorResponse(String errormessage);
    }

}
