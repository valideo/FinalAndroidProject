package vbr.ynov.com.projetschools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class menuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void OnMenuMapClick(View btnMenu_Map){
        Intent EntryActivityIntent = new Intent(menuActivity.this, mapActivity.class);
        startActivity(EntryActivityIntent);
    }

    public void OnListMapClick(View btnMenu_List){
        Intent EntryActivityIntent = new Intent(menuActivity.this, listActivity.class);
        startActivity(EntryActivityIntent);
    }

    public void OnConfigClick(View btnMenu_Config){
        Intent EntryActivityIntent = new Intent(menuActivity.this, configActivity.class);
        startActivity(EntryActivityIntent);
    }

    public void OnCreateClick(View btnMenu_Add){
        Intent EntryActivityIntent = new Intent(menuActivity.this, createActivity.class);
        startActivity(EntryActivityIntent);
    }
}
