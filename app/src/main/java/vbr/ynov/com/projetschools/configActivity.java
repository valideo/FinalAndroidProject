package vbr.ynov.com.projetschools;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class configActivity extends AppCompatActivity {

    private static SharedPreferences configPreferences;
    private SharedPreferences.Editor configPrefsEditor;
    private ImageButton imgBtnPublic;
    private ImageButton ImgBtnPrivate;

    String isPublicPrivate = configPreferences.getString("isPublicPrivate", null);
    public String valueTest = getSharedPreferences("text", 0).getString("isPublicPrivate",null);
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        imgBtnPublic = (ImageButton) findViewById(R.id.publicSchoolsBtn);
        ImgBtnPrivate = (ImageButton) findViewById(R.id.privateSchoolsBtn);
        initializeTopBar();
        initializeButtons();
    }

    public void initializeButtons(){
        if(isPublicPrivate == "0"){
            imgBtnPublic.setColorFilter(Color.argb(50,0,0,0));
        }else if(isPublicPrivate == "1"){

        }else if(isPublicPrivate == "2"){

        }
    }

    public void initializeTopBar(){

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
        finish();
    }
}
