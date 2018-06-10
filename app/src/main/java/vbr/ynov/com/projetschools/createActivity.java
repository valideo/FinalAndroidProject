package vbr.ynov.com.projetschools;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class createActivity extends AppCompatActivity {

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
    PostDataAsync postDataAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        MainMethods.initializeTopBar(findViewById(android.R.id.content), createActivity.this, "Création", "#aa66cc", false);
         ui_name = (EditText) findViewById(R.id.editTxtName);
         ui_address = (EditText) findViewById(R.id.editTxtAddress);
         ui_open = (EditText) findViewById(R.id.editTxtOpen);
         ui_phone = (EditText) findViewById(R.id.editTxtPhone);
         ui_email = (EditText) findViewById(R.id.editTxtEmail);
         ui_lat = (EditText) findViewById(R.id.editTxtLat);
         ui_lng = (EditText) findViewById(R.id.editTxtLng);
         ui_nbEtu = (EditText) findViewById(R.id.editTxtNbEtu);
         ui_status = (ToggleButton) findViewById(R.id.toggleStatus);
         ui_validate = (Button) findViewById(R.id.btnValidate);
         setBtnValid();
    }

    public void setBtnValid(){
        final View.OnClickListener listenerManager = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDataAsync =new PostDataAsync();
                postDataAsync.execute();
            }
        };
        ui_validate.setOnClickListener(listenerManager);
    }

    public class PostDataAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.toLowerCase().contains("school".toLowerCase())){
                MainMethods.showAlert(createActivity.this ,"L'école a bien été ajoutée.");
                MainMethods.returnToMenu(createActivity.this);

            }else {
                MainMethods.showAlert(createActivity.this,"Echec lors de l'ajout de l'école. Vérifiez que tous les champs sont remplis et corrects.");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String json = onValidClick();
            String urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools";
            loginActivity.Services serviceAPI = new loginActivity.Services();
            try {
                String postResponse = serviceAPI.doPostRequest(urlWebService, json);
                return postResponse;
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



}
