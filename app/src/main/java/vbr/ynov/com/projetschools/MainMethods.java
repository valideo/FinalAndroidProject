package vbr.ynov.com.projetschools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by valen on 08/06/2018.
 */

public class MainMethods {

    public static void showAlert(Context context, String text){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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

    public static void initializeTopBar(View v, final Context context, String title, String colorBar, Boolean isRightBtnVisible){

        TextView txtBar = (TextView) v.findViewById(R.id.menuTitleBar);
        ImageButton rightBtnBar = (ImageButton) v.findViewById(R.id.menuBtnRight);
        ImageButton leftBtnBar = v.findViewById(R.id.menuBtnLeft);
        LinearLayout llBar =(LinearLayout)v.findViewById(R.id.layoutTopBar);

        if(isRightBtnVisible == false)
            rightBtnBar.setVisibility(View.INVISIBLE);
        else
            rightBtnBar.setVisibility(View.VISIBLE);

        txtBar.setText(title);
        llBar.setBackgroundColor(Color.parseColor(colorBar));

        final View.OnClickListener listenerManager = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMenu(context);
            }
        };
        leftBtnBar.setOnClickListener(listenerManager);
    }

    public static void goToList(Context context){
        Intent intent = new Intent(context, listActivity.class);
        context.startActivity(intent);
    }

    public static void returnToMenu(Context context){
        ((Activity)context).finish();
    }

}
