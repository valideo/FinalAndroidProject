package vbr.ynov.com.projetschools;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class detailActivity extends AppCompatActivity {

    private String schoolId;
    private EditText ui_name;
    private EditText ui_address;
    private EditText ui_open;
    private EditText ui_phone;
    private EditText ui_email;
    private EditText ui_lat;
    private EditText ui_lng;
    private EditText ui_nbEtu;
    private ToggleButton ui_status;
    private Button ui_validate;
    private String urlWebService;
    private ImageButton btnUpdateSchool;
    private ImageButton btnOpenMap;
    UpdateDataAsync updateDataAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        schoolId = intent.getStringExtra("id");
        setContentView(R.layout.activity_create);
        MainMethods.initializeTopBar(findViewById(android.R.id.content), detailActivity.this, "Détail Ecole", "#ff9900", true);
        ui_name = (EditText) findViewById(R.id.editTxtName);
        ui_name.setEnabled(false);
        ui_address = (EditText) findViewById(R.id.editTxtAddress);
        ui_address.setEnabled(false);
        ui_open = (EditText) findViewById(R.id.editTxtOpen);
        ui_open.setEnabled(false);
        ui_phone = (EditText) findViewById(R.id.editTxtPhone);
        ui_phone.setEnabled(false);
        ui_email = (EditText) findViewById(R.id.editTxtEmail);
        ui_email.setEnabled(false);
        ui_lat = (EditText) findViewById(R.id.editTxtLat);
        ui_lat.setEnabled(false);
        ui_lng = (EditText) findViewById(R.id.editTxtLng);
        ui_lng.setEnabled(false);
        ui_nbEtu = (EditText) findViewById(R.id.editTxtNbEtu);
        ui_nbEtu.setEnabled(false);
        ui_status = (ToggleButton) findViewById(R.id.toggleStatus);
        ui_status.setEnabled(false);
        ui_validate = (Button) findViewById(R.id.btnValidate);
        ui_validate.setVisibility(View.INVISIBLE);
        urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools/" + schoolId;
        new detailActivity.loadSchoolAsyncTask().execute();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setRightBtnBar();
    }

    public void setRightBtnBar(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup);
        ImageButton rightBtnBar = (ImageButton) findViewById(R.id.menuBtnRight);
        final View.OnClickListener listenerManager = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        };
        rightBtnBar.setOnClickListener(listenerManager);

        btnOpenMap = (ImageButton) dialog.findViewById(R.id.btnGoMap);
        btnUpdateSchool = (ImageButton) dialog.findViewById(R.id.btnEditSchool);

        final View.OnClickListener listenerManagerPopup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnGoMap:
                        dialog.dismiss();
                        Intent intentMap = new Intent(detailActivity.this, mapActivity.class);
                        intentMap.putExtra("idSchool", schoolId);
                        startActivity(intentMap);
                        break;
                    case R.id.btnEditSchool:
                        dialog.dismiss();
                        goToEditMode();
                        break;
                }
            }
        };
        btnOpenMap.setOnClickListener(listenerManagerPopup);
        btnUpdateSchool.setOnClickListener(listenerManagerPopup);
    }

    private void goToEditMode(){
        ui_validate.setVisibility(View.VISIBLE);
        ui_validate.setText("Valider");
        final View.OnClickListener listenerManagerEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataAsync =new UpdateDataAsync();
                updateDataAsync.execute();
            }
        };
        ui_validate.setOnClickListener(listenerManagerEdit);
        ui_name.setEnabled(true);
        ui_address.setEnabled(true);
        ui_open.setEnabled(true);
        ui_phone.setEnabled(true);
        ui_email.setEnabled(true);
        ui_lat.setEnabled(true);
        ui_lng.setEnabled(true);
        ui_nbEtu.setEnabled(true);
        ui_status.setEnabled(true);
    }


    public class UpdateDataAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.toLowerCase().contains("school".toLowerCase())){
                finish();
                startActivity(getIntent());


            }else {
                MainMethods.showAlert(detailActivity.this,"Echec lors de la modification de l'école. Vérifiez que tous les champs sont remplis et corrects.");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String json = onValidClick();
            String urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools/"+schoolId;
            loginActivity.Services serviceAPI = new loginActivity.Services();
            try {
                String patchResponse = serviceAPI.doUpdateRequest(urlWebService, json);
                return patchResponse;
            } catch (IOException e) {
                return "";
            }
        }
    }

    public  String onValidClick(){
        String nameValue = ui_name.getText().toString();
        String addressValue = ui_address.getText().toString();
        String openValue = ui_open.getText().toString();
        String phoneValue = ui_phone.getText().toString();
        String emailValue = ui_email.getText().toString();

        Double latValue = 0.0;
        Double lngValue = 0.0;
        if(!"".equals(ui_lat.getText().toString()) && !"".equals(ui_lng.getText().toString())){
            latValue = Double.parseDouble(ui_lat.getText().toString());
            lngValue = Double.parseDouble(ui_lng.getText().toString());
        }

        Integer nbEtuValue = 0;
        if(!"".equals(ui_nbEtu.getText().toString()))
            nbEtuValue = Integer.parseInt(ui_nbEtu.getText().toString());

        String statusValue = ui_status.getText().toString();

        if(!"".equals(nameValue) && !"".equals(addressValue) && !"".equals(emailValue) && latValue != 0.0 && lngValue != 0.0 && nbEtuValue != 0 && !"".equals(statusValue)){

            JSONObject jsonToPush = new JSONObject();
            try {
                jsonToPush.put("name", nameValue);
                jsonToPush.put("address", addressValue);
                jsonToPush.put("status", statusValue);
                jsonToPush.put("longitude", lngValue);
                jsonToPush.put("latitude", latValue);
                jsonToPush.put("students_count", nbEtuValue);
                jsonToPush.put("email", emailValue);
                return jsonToPush.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            return "";
        }
        return "";
    }

    public class loadSchoolAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            showSchool(s);
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

    private void showSchool(String json){

        String schoolName = "";
        String schoolAddress = "";
        String schoolOpen = "";
        String schoolPhone = "";
        String schoolEmail = "";
        String schoolLat = "";
        String schoolLng = "";
        String schoolNbEtu = "";
        String schoolStatus = "";

        try {
            JSONObject school = new JSONObject(json);
            JSONObject schoolObject = school.getJSONObject("school");
            schoolName = schoolObject.getString("name");
            schoolAddress = schoolObject.getString("address");
            schoolEmail = schoolObject.getString("email");
            schoolLat = schoolObject.getString("latitude");
            schoolLng = schoolObject.getString("longitude");
            schoolNbEtu = schoolObject.getString("students_count");
            schoolOpen = schoolObject.getString("opening_hours");
            schoolPhone = schoolObject.getString("phone");
            schoolStatus = schoolObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ui_name.setText(schoolName);
        ui_address.setText(schoolAddress);
        ui_email.setText(schoolEmail);
        ui_lat.setText(schoolLat);
        ui_lng.setText(schoolLng);
        ui_phone.setText(schoolPhone);
        ui_open.setText(schoolOpen);
        if("public".equals(schoolStatus))
            ui_status.setChecked(true);
        else
            ui_status.setChecked(false);
        ui_nbEtu.setText(schoolNbEtu);
    }
}
