package vbr.ynov.com.projetschools;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class loginActivity extends AppCompatActivity {

    //On défini toutes les variables nécessaires à cette activity
    private EditText ui_MdpLabel;
    private EditText ui_UsernameLabel;
    private CheckBox ui_saveLoginCheckBox;
    private static SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    String urlWebService = "";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // On associe nos variables créées plus haut à nos éléments visuels de l'app
        ui_MdpLabel = (EditText) findViewById(R.id.txtMdp);
        ui_UsernameLabel = (EditText) findViewById(R.id.txtUsername);
        ui_saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        //S'il existe des identifiants enregistrés, on les rempli dans le formulaire
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            ui_UsernameLabel .setText(loginPreferences.getString("username", ""));
            ui_MdpLabel.setText(loginPreferences.getString("password", ""));
            ui_saveLoginCheckBox.setChecked(true);
        }
    }

    public class AsyncTask extends android.os.AsyncTask<String, String, String >
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            //Si la valeur retournée est "true" on autorise la connexion et on lance une seconde activité sinon une alerte apparait

            try{
                JSONObject authObject = new JSONObject(s);
                String success = authObject.getString("success");

                if(success.equals("true")){
                    String auth_token = authObject.getString("auth_token");
                    loginPrefsEditor.putString("auth_token", auth_token);
                    loginPrefsEditor.commit();
                    Intent EntryActivityIntent = new Intent(loginActivity.this, menuActivity.class);
                    startActivity(EntryActivityIntent);
                }
                else {
                    showAlert("Authentification échouée");
                }
            }
            catch (Exception e){
                System.out.println(e);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Services serviceAPI = new Services();
                String json = serviceAPI.loginJson(ui_UsernameLabel.getText().toString(), ui_MdpLabel.getText().toString());
                String postResponse = serviceAPI.doPostRequest("https://schoolz-api.herokuapp.com/api/v1/users/sign_in", json);
                return postResponse;
            }
            catch (Exception e){
                System.out.println(e);
            }

            return null;
        }
    }

    //Fonction qui sert à diplay des alertes
    public void showAlert(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(loginActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //Au click du bouton "vider", on vide les champs username et mot de passe
    public void onCleanClick(View cleanButton){
        ui_UsernameLabel.setText("");
        ui_MdpLabel.setText("");
    }


    public void OnValidClick(View validButton){
        //On check si tous les champs sont bien remplis
        if(TextUtils.isEmpty(ui_UsernameLabel.getText()) && TextUtils.isEmpty(ui_MdpLabel.getText())){
            showAlert("Merci de remplir les champs !");
        }
        else if(TextUtils.isEmpty(ui_UsernameLabel.getText())){
            showAlert("Merci d'entrer un nom d'utilisateur !");
        }
        else if(TextUtils.isEmpty(ui_MdpLabel.getText())){
            showAlert("Merci d'entrer un mot de passe !");
        }
        //Si c'est ok on éxécute le code sinon on affiche une alerte
        else {

            //Si box check , on save les identifiants
            if (ui_saveLoginCheckBox.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", ui_UsernameLabel.getText().toString());
                loginPrefsEditor.putString("password", ui_MdpLabel.getText().toString());
                loginPrefsEditor.commit();
                //Sinon on vide les préférences enregistrées (au cas où il en existe)
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }

            //On défini l'url à accéder pour tester le login et on execute la fonction asynchrone
            new AsyncTask().execute();
        }
    }

    public static class Services {

        OkHttpClient client = new OkHttpClient();

        // code request code here
        String doGetRequest(String url) throws IOException {
            String token = loginPreferences.getString("auth_token", "");
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("AUTHORIZATION", loginPreferences.getString("auth_token", ""))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        // post request code here

        public final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        // test data
        String loginJson(String email, String password) {
            return "{\"email\": \"" + email + "\","
                    + "\"password\": \"" + password + "\""
                    + "}";
        }

        String doPostRequest(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        /*
        public void main(String[] args) throws IOException {

            // issue the Get request
            TestMain example = new TestMain();
            String getResponse = example.doGetRequest("http://www.vogella.com");
            System.out.println(getResponse);


            // issue the post request

            String json = example.loginJson("Jesse", "Jake");
            String postResponse = example.doPostRequest("http://www.roundsapp.com/post", json);
            System.out.println(postResponse);
        }   */

    }

}
