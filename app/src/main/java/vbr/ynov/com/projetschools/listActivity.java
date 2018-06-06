package vbr.ynov.com.projetschools;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class listActivity extends AppCompatActivity {

    private ListView lstView;
    private SchoolsListAdapter adapter;
    private List<Schools> mSchoolsList;
    String urlWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initializeTopBar();
        lstView = (ListView) findViewById(R.id.lstView);
        urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools";
        new listActivity.loadDataAsyncTask().execute();
    }

    public void initializeTopBar(){

        TextView txtBar = (TextView) findViewById(R.id.menuTitleBar);
        txtBar.setText("Liste des écoles");

        ImageButton rightBtnBar = (ImageButton) findViewById(R.id.menuBtnRight);
        rightBtnBar.setVisibility(View.INVISIBLE);

        ImageButton leftBtnBar = findViewById(R.id.menuBtnLeft);
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
        leftBtnBar.setOnClickListener(listenerManager);

        LinearLayout llBar =(LinearLayout)findViewById(R.id.layoutTopBar);
        llBar.setBackgroundColor(Color.parseColor("#ff8800"));
    }

    public void returnToMenu(){
        finish();
    }

    public class loadDataAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            showList(s);
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

    public void showList(String Json){
        Json = Json.replace("{\"schools\":", "");
        Json = (Json.substring(0, Json.length() - 1));
        lstView = (ListView)findViewById(R.id.lstView);
        mSchoolsList = new ArrayList<>();

        try {
            JSONArray arrayValues = new JSONArray(Json);
            for (int i=0; i < arrayValues.length(); i++) {
                JSONObject schoolObject = arrayValues.getJSONObject(i);
                Double longitude = Double.parseDouble(schoolObject.getString("longitude"));
                Double latitude = Double.parseDouble(schoolObject.getString("latitude"));
                Integer nbEleves = Integer.parseInt(schoolObject.getString("students_count"));
                String name = schoolObject.getString("name");
                String address = schoolObject.getString("address");
                String status = schoolObject.getString("status");

                mSchoolsList.add(new Schools(longitude, latitude, name, status, address, nbEleves));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    adapter = new SchoolsListAdapter(getApplicationContext(), mSchoolsList);
    lstView.setAdapter(adapter);
    }
}
