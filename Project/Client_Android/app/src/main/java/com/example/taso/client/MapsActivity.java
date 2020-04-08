package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Image_Downloader.OnDownloadUpdateListener act;
    LatLng poiloc;
    ArrayList<Poi> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        data = (ArrayList<Poi>)getIntent().getSerializableExtra("PoiList2");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(int i=0; i<data.size()-1; i++){
            poiloc = new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(poiloc).title(data.get(i).getName()).snippet(data.get(i).getCategory()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(poiloc));
        }
        poiloc = new LatLng(data.get(data.size()-1).getLatitude(), data.get(data.size()-1).getLongitude());
        mMap.addMarker(new MarkerOptions().position(poiloc).title(data.get(data.size()-1).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(poiloc));
    }

}


