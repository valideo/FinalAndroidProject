package vbr.ynov.com.projetschools;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import vbr.ynov.com.projetschools.R;
import vbr.ynov.com.projetschools.Schools;

/**
 * Created by valen on 05/06/2018.
 */

public class SchoolsListAdapter extends BaseAdapter implements LocationListener {

    private Context mContext;
    private List<Schools> mSchoolsList;
    private Double schoolLat = 0.0;
    private Double schoolLng = 0.0;
    private Double myLat = 0.0;
    private Double myLng = 0.0;
    private float[] distance = new float [1];
    LocationManager locationManager;
    LocationListener locationListener;


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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.listitem, null);

        TextView schoolName = (TextView)v.findViewById(R.id.txtName);
        TextView schoolAdress = (TextView)v.findViewById(R.id.txtAddress);
        TextView schoolNbEleves = (TextView)v.findViewById(R.id.txtNbEleves);
        TextView kmFormCurrentPos = (TextView)v.findViewById(R.id.txtnbKm);
        ImageView btnOKKO = (ImageView) v.findViewById(R.id.OKKObtn);
        ImageButton btnOpenMap = (ImageButton)v.findViewById(R.id.btnMapList);
        ImageButton btnDeleteSchool = (ImageButton)v.findViewById(R.id.btnMapDel);
        LinearLayout layoutRowSchool = (LinearLayout)v.findViewById(R.id.RowSchoolItem);


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

        Location.distanceBetween(schoolLat, schoolLng, myLat, myLng, distance);
        layoutRowSchool.setBackgroundColor(Color.parseColor(bgColor));
        btnOpenMap.setImageResource(R.drawable.ic_map);
     //   btnDeleteSchool.setImageResource();
        schoolName.setText(mSchoolsList.get(position).getName());
        schoolAdress.setText(mSchoolsList.get(position).getAddress());
        kmFormCurrentPos.setText(Float.toString(Math.round(distance[0]/1000))+" Km");
        schoolNbEleves.setText(String.valueOf(nbEleves)+ " élèves");
        return v;
    }

    @Override
    public void onLocationChanged(Location location) {
        myLat = location.getLatitude();
        myLng = location.getLongitude();
    }
}
