package vbr.ynov.com.projetschools;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class listActivity extends AppCompatActivity {

    private ListView lstView;
    private SchoolsListAdapter schoolAdapter;
    private List<Schools> mSchoolsList;
    private static SharedPreferences configPreferences;
    private SharedPreferences.Editor configPrefsEditor;
    private String isPublicPrivate;
    String urlWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        MainMethods.initializeTopBar(findViewById(android.R.id.content), listActivity.this, "Liste des écoles", "#ff8800", false);
        lstView = (ListView) findViewById(R.id.lstView);
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

        new listActivity.loadDataAsyncTask().execute();
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
                Integer id = Integer.parseInt(schoolObject.getString("id"));
                Double longitude = Double.parseDouble(schoolObject.getString("longitude"));
                Double latitude = Double.parseDouble(schoolObject.getString("latitude"));
                Integer nbEleves = Integer.parseInt(schoolObject.getString("students_count"));
                String name = schoolObject.getString("name");
                String address = schoolObject.getString("address");
                String status = schoolObject.getString("status");

                mSchoolsList.add(new Schools(id, longitude, latitude, name, status, address, nbEleves));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    schoolAdapter = new SchoolsListAdapter(getApplicationContext(), mSchoolsList);
    lstView.setAdapter(schoolAdapter);
    }
}
