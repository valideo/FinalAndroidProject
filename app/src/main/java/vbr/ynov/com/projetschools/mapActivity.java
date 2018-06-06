package vbr.ynov.com.projetschools;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class mapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap myMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQ_LOCATION_CODE = 99;
    private ImageButton btnBarLeft;
    public static String urlWebService = "";
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupView();
        urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools";
        new mapActivity.AsyncMapTasks().execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

   public class AsyncMapTasks extends AsyncTask<String, String, String>{

       @Override
       protected void onPreExecute() {
           super.onPreExecute();
       }

       @Override
       protected void onPostExecute(String s) {
           showSchools(s);
       }

       @Override
       protected String doInBackground(String... strings) {
           try {
               loginActivity.Services serviceAPI = new loginActivity.Services();
               String getResponse = serviceAPI.doGetRequest(urlWebService);
               return getResponse;
           }
           catch (Exception e){
               System.out.println(e);
           }
           return null;
       }
   }

    protected void setupView(){
        btnBarLeft = findViewById(R.id.menuBtnLeft);
        final View.OnClickListener listenerManager = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.menuBtnLeft:returnToMenu();
                    break;

                    default:break;
                }
            }
        };

        btnBarLeft.setOnClickListener(listenerManager);
    }

    public void returnToMenu(){
        finish();
    }

    public void showSchools(String json){
        try {
            json = json.replace("{\"schools\":", "");
            json = (json.substring(0, json.length() - 1));
            JSONArray arrayValues = new JSONArray(json);
            for (int i=0; i < arrayValues.length(); i++) {
                JSONObject schoolObject = arrayValues.getJSONObject(i);
                Double longitude = Double.parseDouble(schoolObject.getString("longitude"));
                Double latitude = Double.parseDouble(schoolObject.getString("latitude"));
                Integer nbEleves = Integer.parseInt(schoolObject.getString("students_count"));
                InsertSchoolMarkersOnMap(longitude, latitude, nbEleves);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void InsertSchoolMarkersOnMap(Double lng, Double lat, Integer nbEleves){

        LatLng markerLatLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(markerLatLng);
        if(nbEleves <= 50){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_rouge));
        }else if(nbEleves > 50 && nbEleves <= 200){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_vert));
        }
        myMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQ_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        myMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            myMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("loc", myLatLng.toString());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLatLng);
        markerOptions.title("Je suis ici");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_position));
        myMap.getUiSettings().setMyLocationButtonEnabled(false);

        currentLocationMarker = myMap.addMarker(markerOptions);

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 6.5f), 1000, null);

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    public void OnBackClick(View btnMenu_Map){
        Intent EntryActivityIntent = new Intent(mapActivity.this, menuActivity.class);
        startActivity(EntryActivityIntent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQ_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQ_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}