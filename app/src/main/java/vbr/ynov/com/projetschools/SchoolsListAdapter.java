package vbr.ynov.com.projetschools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by valen on 05/06/2018.
 */

public class SchoolsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Schools> mSchoolsList;
    private Double schoolLat = 0.0;
    private Double schoolLng = 0.0;
    private Double myLat = 0.0;
    private Double myLng = 0.0;
    private LatLng myLatLng = mapActivity.myLatLng;
    private float[] distance = new float [1];
    deleteAsyncTask DelAsync;
    LinearLayout layoutRowSchool;


    public SchoolsListAdapter(Context mContext, List<Schools> mSchoolsList) {
        this.mContext = mContext;
        this.mSchoolsList = mSchoolsList;
    }

    @Override
    public int getCount() {
        return mSchoolsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSchoolsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.listitem, null);

        TextView schoolName = (TextView)v.findViewById(R.id.txtName);
        TextView schoolAdress = (TextView)v.findViewById(R.id.txtAddress);
        TextView schoolNbEleves = (TextView)v.findViewById(R.id.txtNbEleves);
        TextView kmFormCurrentPos = (TextView)v.findViewById(R.id.txtnbKm);
        ImageView btnOKKO = (ImageView) v.findViewById(R.id.OKKObtn);
        ImageButton btnOpenMap = (ImageButton)v.findViewById(R.id.btnMapList);
        ImageButton btnDeleteSchool = (ImageButton)v.findViewById(R.id.btnMapDel);
        layoutRowSchool = (LinearLayout)v.findViewById(R.id.RowSchoolItem);


        Integer nbEleves = mSchoolsList.get(position).getNbEleves();
        schoolLat = mSchoolsList.get(position).getLatitude();
        schoolLng = mSchoolsList.get(position).getLongitude();
        String bgColor = "#";
        if(nbEleves <= 50){
            btnOKKO.setImageResource(R.drawable.ic_ko);
            bgColor+= "cf2a27";
        }else if(nbEleves > 50 && nbEleves <= 200){
            btnOKKO.setImageResource(R.drawable.ic_ok);
            bgColor+= "ff9900";
        }
        else {
            btnOKKO.setImageResource(R.drawable.ic_ok);
            bgColor+= "009e0f";
        }

        Location.distanceBetween(schoolLat, schoolLng, myLatLng.latitude, myLatLng.longitude, distance);
        layoutRowSchool.setBackgroundColor(Color.parseColor(bgColor));
        btnOpenMap.setImageResource(R.drawable.ic_map);
        btnDeleteSchool.setImageResource(R.drawable.ic_delete);
        schoolName.setText(mSchoolsList.get(position).getName());
        schoolAdress.setText(mSchoolsList.get(position).getAddress());
        kmFormCurrentPos.setText(Float.toString(Math.round(distance[0]/1000))+" Km");
        schoolNbEleves.setText(String.valueOf(nbEleves)+ " élèves");

        final View.OnClickListener listenerManager = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnMapDel:
                        DelAsync =new deleteAsyncTask();
                        DelAsync.execute(mSchoolsList.get(position).getId().toString());
                        ;break;
                    case R.id.btnMapList:
                        Intent intentMap = new Intent(mContext,mapActivity.class);
                        intentMap.putExtra("idSchool", mSchoolsList.get(position).getId().toString());
                        mContext.startActivity(intentMap);
                }
            }
        };
// J'ai créé un second listener manager car j'ai un bug, quand je met tout dans le même, quand on clic sur la row, ça lance 2 activity
        final View.OnClickListener listenerManager2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.RowSchoolItem:
                        Intent intentDetail = new Intent(mContext,detailActivity.class);
                        intentDetail.putExtra("id", mSchoolsList.get(position).getId().toString());
                        mContext.startActivity(intentDetail);
                }
            }
        };

        btnDeleteSchool.setOnClickListener(listenerManager);
        layoutRowSchool.setOnClickListener(listenerManager2);
        btnOpenMap.setOnClickListener(listenerManager);

        return v;
    }

    public class deleteAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
        }


        @Override
        protected String doInBackground(String... strings) {
            String id = strings[0];
            if(!"".equals(id)) {
                String json = "";
                String urlWebService = "https://schoolz-api.herokuapp.com/api/v1/schools/"+ id;
                loginActivity.Services serviceAPI = new loginActivity.Services();
                try {
                    String postResponse = serviceAPI.doDeleteRequest(urlWebService, json);
                    return postResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }
            return "";
        }
    }
}
