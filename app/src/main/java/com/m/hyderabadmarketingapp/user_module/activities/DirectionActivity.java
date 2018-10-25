package com.m.hyderabadmarketingapp.user_module.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.m.hyderabadmarketingapp.R;
import com.m.hyderabadmarketingapp.user_module.models.DataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DirectionActivity extends FragmentActivity implements OnMapReadyCallback,DirectionCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Polyline polyline;
    LocationRequest mLocationRequest;

    double source_lat,source_long,destination_lat,destination_long,currentLatitude,currentLongitude;
    private LatLng sourcelatlng,destinationlatlng;
    public static String serverKey = "AIzaSyDeeaDQcCB9Ep-8OKDGhoMTR7zcLTYlGrg";
    Timer timer;
    int delay = 0; // delay for 0 sec.
    int period = 3000; // repeat every 10 sec.
    private LatLng origin;
    String distance;
    JSONArray stepsarray;
    String duration;

    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.tv_title)
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        source_lat = Double.parseDouble(intent.getStringExtra("souce_lat"));
        source_long = Double.parseDouble(intent.getStringExtra("source_long"));
        destination_lat = Double.parseDouble(intent.getStringExtra("destination_lat"));
        destination_long = Double.parseDouble(intent.getStringExtra("destination_long"));
        tv_title.setText(intent.getStringExtra("merchant_name"));
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

       if(!checkingPermissionAreEnabledOrNot())
           requestMultiplePermission();

        // Initializing
        MarkerPoints = new ArrayList<>();
        sourcelatlng=new LatLng(source_lat,source_long);
        destinationlatlng=new LatLng(destination_lat,destination_long);
        Log.e("destination latng",""+destinationlatlng.latitude+" "+destinationlatlng.longitude);
        MarkerPoints.add(sourcelatlng);
        MarkerPoints.add(destinationlatlng);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        try {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            origin = new LatLng(currentLatitude, currentLongitude);

                            try {
                                requestDirection(origin);
                            } catch (Exception e) {
                                Log.e("Exception for",e.toString());
                            }
                        }
                    });
                }
            }, delay, period);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.clear();
        //Initialize Google Play Services

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        //new code
        MarkerOptions options_source = new MarkerOptions();
        options_source.position(MarkerPoints.get(0));
        options_source.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(options_source);

        MarkerOptions options_destination = new MarkerOptions();
        options_destination.position(MarkerPoints.get(1));
        options_destination.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options_destination);

        if (MarkerPoints.size() >= 2) {
            LatLng origin = MarkerPoints.get(0);
            LatLng dest = MarkerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getUrl(origin, dest);
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void requestDirection(LatLng latt) {

        GoogleDirection.withServerKey(serverKey)
                .from(latt)
                .to(destinationlatlng)
                // .optimizeWaypoints(true)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if (direction.isOK()) {

            Log.d("DIRECTIONLIST", "" + destinationlatlng);
            Log.d("DIRECTIONLIST", "" + origin);
            Log.d("RESSSSS", "" + rawBody);

            try {
                destinationlatlng = destinationlatlng;

                JSONObject job = new JSONObject(rawBody);
                Log.e("Response from maps",rawBody);
                JSONArray array = job.getJSONArray("routes");
                stepsarray = array.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                distance = stepsarray.getJSONObject(0).getJSONObject("distance").getString("value");
                duration = stepsarray.getJSONObject(0).getJSONObject("duration").getString("value");

                polyline = null;
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();Log.d("POLYLINEEEEEEE", "" + polyline);
                if (polyline == null) {
                    mMap.clear();
                    polyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLUE));
                }
                else {
                    mMap.clear();
                    polyline.remove();
                    polyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLUE));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.e("on location","onLocationChanged");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean checkingPermissionAreEnabledOrNot() {
        int coarseloc = ContextCompat.checkSelfPermission(DirectionActivity.this, ACCESS_COARSE_LOCATION);
        int accessloc = ContextCompat.checkSelfPermission(DirectionActivity.this, ACCESS_FINE_LOCATION);
        return coarseloc == PackageManager.PERMISSION_GRANTED && accessloc == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(DirectionActivity.this, new String[]
                {
                        ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION
                }, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }else {
                        Toast.makeText(DirectionActivity.this,"Location permission is mandatory to use this app.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(timer!=null)
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null)
        timer.cancel();
    }
}