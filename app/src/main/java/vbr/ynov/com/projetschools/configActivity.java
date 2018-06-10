package vbr.ynov.com.projetschools;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class configActivity extends AppCompatActivity {

    private static SharedPreferences configPreferences;
    private SharedPreferences.Editor configPrefsEditor;
    private ImageButton imgBtnPublic;
    private ImageButton ImgBtnPrivate;
    private String isPublicPrivate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        this.imgBtnPublic = (ImageButton) findViewById(R.id.publicSchoolsBtn);
        this.ImgBtnPrivate = (ImageButton) findViewById(R.id.privateSchoolsBtn);

        configPreferences = getSharedPreferences("configPrefs", MODE_PRIVATE);
        configPrefsEditor = configPreferences.edit();
        isPublicPrivate = configPreferences.getString("isPublicPrivate", "0");

        initializeTopBar();
        initializeButtons();
    }

    //Ispublicprivate = 0 => on afiche tout - =1 => on affiche que public - =2 => on affiche que privé - =3 quand aucun selectionné

    public void initializeButtons(){ //Set l'opacité des btn

        if("0".equals(isPublicPrivate)){
            this.imgBtnPublic.setAlpha(100);
            this.ImgBtnPrivate.setAlpha(100);
        }else if("1".equals(isPublicPrivate)){
            this.imgBtnPublic.setAlpha(100);
            this.ImgBtnPrivate.setAlpha(20);
        }else if("2".equals(isPublicPrivate)){
            this.imgBtnPublic.setAlpha(20);
            this.ImgBtnPrivate.setAlpha(100);
        }else if("3".equals(isPublicPrivate)){
            this.imgBtnPublic.setAlpha(20);
            this.ImgBtnPrivate.setAlpha(20);
        }
    }

    public void OnclickBtnPrivate(View privateSchoolsBtn){

        if("0".equals(isPublicPrivate)){
            commitConf("1");
        }else if("1".equals(isPublicPrivate)){
            commitConf("0");
        }else if("2".equals(isPublicPrivate)){
            commitConf("3");
        }else if("3".equals(isPublicPrivate)){
            commitConf("2");
        }
    }

    public void OnclickBtnPublic(View publicSchoolsBtn){

        if("0".equals(isPublicPrivate)){
            commitConf("2");
        }else if("1".equals(isPublicPrivate)){
            commitConf("3");
        }else if("2".equals(isPublicPrivate)){
            commitConf("0");
        }else if("3".equals(isPublicPrivate)){
            commitConf("1");
        }
    }

    public void commitConf(String value){ //Actualise valeur de variable global configuration
        configPrefsEditor.putString("isPublicPrivate", value);
        configPrefsEditor.commit();
        isPublicPrivate = configPreferences.getString("isPublicPrivate", "0");
        initializeButtons();
    }

    public void initializeTopBar(){ //

        TextView txtBar = (TextView) findViewById(R.id.menuTitleBar);
        txtBar.setText("Configuration");

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
        llBar.setBackgroundColor(Color.parseColor("#ffbb32"));
    }

    public void returnToMenu(){
        if("3".equals(isPublicPrivate)){
            MainMethods.showAlert(this,"Vous devez séléctionner au moins un type de données à afficher");
        }
        else {
            finish();
        }
    }
}
