package vbr.ynov.com.projetschools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class mapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap myMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQ_LOCATION_CODE = 99;
    private ImageButton btnBarLeft;
    private String urlWebServiceSchool = "";
    public static String urlWebService = "";
    private String jsonSchool = "";
    public static LatLng myLatLng = new LatLng(45.75, 4.85);
    private static SharedPreferences configPreferences;
    private SharedPreferences.Editor configPrefsEditor;
    private String isPublicPrivate;
    private EditText searchBar;
    private Double schoolLat = 0.0;
    private Double schoolLng = 0.0;
    private float[] distance = new float [1];
    private String schoolStrName;
    private String schoolStrAddress;
    private String schoolStrNbEleves;
    String schoolId;
    getSchoolAsync getschoolasync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        schoolId = intent.getStringExtra("idSchool");
        if(schoolId != null){
            urlWebServiceSchool = "https://schoolz-api.herokuapp.com/api/v1/schools/"+ schoolId;
            new mapActivity.getSchoolAsync().execute();
        }

        setContentView(R.layout.activity_map);
        configPreferences = getSharedPreferences("configPrefs", MODE_PRIVATE);
        configPrefsEditor = configPreferences.edit();
        isPublicPrivate = configPreferences.getString("isPublicPrivate", "0");
        if("0".equals(isPublicPrivate)){
            urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools";
        }else if("1".equals(isPublicPrivate)){
            urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools?status=public";
        }else if("2".equals(isPublicPrivate)){
            urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools?status=private";
        }else {
            urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools";
        }
        new mapActivity.AsyncMapTasks().execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MainMethods.initializeTopBar(findViewById(android.R.id.content), mapActivity.this, "Carte", "#669900", true);
        setRightBtnBar();
        searchBar = (EditText) findViewById(R.id.searchBar);
        searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    onMapSearch();
                    return true;
                }
                return false;
            }
        });
        }

    private void setRightBtnBar(){
        ImageButton rightBtnBar = (ImageButton) findViewById(R.id.menuBtnRight);
        final View.OnClickListener listenerManager = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToList();
            }
        };
        rightBtnBar.setOnClickListener(listenerManager);
    }

    private void goToList(){
        Intent intent = new Intent(this, listActivity.class);
        startActivity(intent);
    }

    private class AsyncMapTasks extends AsyncTask<String, String, String>{

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

    private LatLng schoolToShow(){
        try {
            JSONObject school = new JSONObject(jsonSchool);
            JSONObject schoolObject = school.getJSONObject("school");
            Double schoolLat = Double.parseDouble(schoolObject.getString("latitude"));
            Double schoolLng = Double.parseDouble(schoolObject.getString("longitude"));
            LatLng schoolLatLng = new LatLng(schoolLat, schoolLng);
            return schoolLatLng;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onMapSearch() {
        EditText locationSearch = (EditText) findViewById(R.id.searchBar);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addressList.size() > 0){
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                myMap.addMarker(new MarkerOptions().position(latLng).title("Résultat"));
                myMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            else
                MainMethods.showAlert(this, "Aucun résultat trouvé");
        }
    }

    private class getSchoolAsync extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                loginActivity.Services serviceAPI = new loginActivity.Services();
                jsonSchool = serviceAPI.doGetRequest(urlWebServiceSchool);
                return jsonSchool;
            }
            catch (Exception e){
                System.out.println(e);
            }
            return null;
        }
    }

    private void showSchools(String json){
        try {
            json = json.replace("{\"schools\":", "");
            json = (json.substring(0, json.length() - 1));
            JSONArray arrayValues = new JSONArray(json);
            for (int i=0; i < arrayValues.length(); i++) {
                JSONObject schoolObject = arrayValues.getJSONObject(i);
                Double longitude = Double.parseDouble(schoolObject.getString("longitude"));
                Double latitude = Double.parseDouble(schoolObject.getString("latitude"));
                Integer nbEleves = Integer.parseInt(schoolObject.getString("students_count"));
                Integer id = Integer.parseInt(schoolObject.getString("id"));
                InsertSchoolMarkersOnMap(longitude, latitude, nbEleves, id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void InsertSchoolMarkersOnMap(Double lng, Double lat, Integer nbEleves,Integer Id){


        Marker myMarker;
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
        myMarker =  myMap.addMarker(markerOptions);
        myMarker.setTag(Id);

        myMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    @SuppressLint("Range")
    @Override
    public boolean onMarkerClick(Marker marker) {
        int id = (int)(marker.getTag());
        urlWebServiceSchool = "https://schoolz-api.herokuapp.com/api/v1/schools/"+ id;
        final LinearLayout bottomBoxMap = (LinearLayout)findViewById(R.id.detailSchoolLayout);
        bottomBoxMap.setVisibility(View.VISIBLE);
        String bgColor = "#";
        TextView schoolName = (TextView)findViewById(R.id.txtName);
        TextView schoolAdress = (TextView)findViewById(R.id.txtAddress);
        TextView schoolNbEleves = (TextView)findViewById(R.id.txtNbEleves);
        TextView kmFormCurrentPos = (TextView)findViewById(R.id.txtnbKm);
        ImageView btnOKKO = (ImageView) findViewById(R.id.OKKObtn);
        ImageButton closeBoxMap = (ImageButton)findViewById(R.id.closeBoxMap);
        float[] distance = new float [1];
        getschoolasync =new getSchoolAsync();
        try {
            String jsonString= new getSchoolAsync().execute().get();
            JSONObject school = new JSONObject(jsonString);
            JSONObject schoolObject = school.getJSONObject("school");
            schoolLat = Double.parseDouble(schoolObject.getString("latitude"));
            schoolLng = Double.parseDouble(schoolObject.getString("longitude"));
            schoolStrName = schoolObject.getString("name");
            schoolStrAddress = schoolObject.getString("address");
            schoolStrNbEleves = schoolObject.getString("students_count");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(Integer.parseInt(schoolStrNbEleves) <= 50){
            btnOKKO.setImageResource(R.drawable.ic_ko);
            bgColor+= "cf2a27";
        }else if(Integer.parseInt(schoolStrNbEleves) > 50 && Integer.parseInt(schoolStrNbEleves) <= 200){
            btnOKKO.setImageResource(R.drawable.ic_ok);
            bgColor+= "ff9900";
        }
        else {
            btnOKKO.setImageResource(R.drawable.ic_ok);
            bgColor+= "009e0f";
        }
        bottomBoxMap.setBackgroundColor(Color.parseColor(bgColor));
        bottomBoxMap.setAlpha(95);
        schoolName.setText(schoolStrName);
        schoolAdress.setText(schoolStrAddress);
        schoolNbEleves.setText(String.valueOf(schoolStrNbEleves)+ " élèves");
        Location.distanceBetween(schoolLat, schoolLng, myLatLng.latitude, myLatLng.longitude, distance);
        kmFormCurrentPos.setText(Float.toString(Math.round(distance[0]/1000))+" Km");

        final View.OnClickListener listenerManager = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBoxMap.setVisibility(View.INVISIBLE);
            }
        };
        closeBoxMap.setOnClickListener(listenerManager);
        return false;
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

        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("loc", myLatLng.toString());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLatLng);
        markerOptions.title("Je suis ici");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_position));
        myMap.getUiSettings().setMyLocationButtonEnabled(false);

        currentLocationMarker = myMap.addMarker(markerOptions);

        if(schoolId != null){
            LatLng schoolLatLng = schoolToShow();
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(schoolLatLng, 16f), 1000, null);
        }else
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 12f), 1000, null);

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    public void centerPositionClick(View centerPositionBtn){
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 12f), 1000, null);
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

    private boolean checkLocationPermission()
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