package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActionsForMastByClient.OnDownloadUpdateListener act;
    TextView txtAsyncResponse;
    EditText usId;
    EditText NofPois;
    EditText Range;
    private LocationManager locationManager;
    private LocationListener listener;
    private Spinner spinner;
    LatLng myGps;
    ArrayList<Poi> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cat_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        txtAsyncResponse = (TextView) findViewById(R.id.txtAsyncResponse);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myGps=new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();
        act = new ActionsForMastByClient.OnDownloadUpdateListener() {
            @Override
            public void OnDownloadDeckFinish(ArrayList<Poi> Response) {
                if(Response.size() == 0){
                    open(txtAsyncResponse);
                }else{
                    data =new ArrayList<Poi>(Response);
                    nextAct(data);
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 10, listener);
    }

    public void action(View view){
        Socket s = null;
        usId= (EditText) findViewById(R.id.editText2);
        NofPois= (EditText) findViewById(R.id.editText);
        Range = (EditText) findViewById(R.id.editText3);
        if(isEmpty(usId) || isEmpty(NofPois)){
            open2(txtAsyncResponse);
        }else{
            if(NofPois.getText().toString().matches("[0-9]+") && usId.getText().toString().matches("[0-9]+")){
                if(Range.getText().toString().matches("[0-9]+")) {
                    int i = Integer.parseInt(usId.getText().toString());
                    int j = Integer.parseInt(NofPois.getText().toString());
                    String category;
                    if (spinner.getSelectedItem().toString().equalsIgnoreCase("Arts and Entertainment")) {
                        category = "Arts & Entertainment";
                    } else if (spinner.getSelectedItem().toString().equalsIgnoreCase("All categories")) {
                        category = "no_category";
                    } else {
                        category = spinner.getSelectedItem().toString();
                    }
                    ActionsForMastByClient mTask = new ActionsForMastByClient(this.getApplicationContext(), act, s, 3, i, new Poi(), j, "10.0.2.2", myGps, category, Integer.parseInt(Range.getText().toString())== 0 ? -700 : Integer.parseInt(Range.getText().toString())); //for local use
                    //ActionsForMastByClient mTask = new ActionsForMastByClient(this.getApplicationContext(), act, s, 3, i, new Poi(), j, "192.168.1.4", myGps, category, Integer.parseInt(Range.getText().toString())== 0 ? -700 : Integer.parseInt(Range.getText().toString()));
                    mTask.execute();
                }else{
                    int i = Integer.parseInt(usId.getText().toString());
                    int j = Integer.parseInt(NofPois.getText().toString());
                    String category;
                    if (spinner.getSelectedItem().toString().equalsIgnoreCase("Arts and Entertainment")) {
                        category = "Arts & Entertainment";
                    } else if (spinner.getSelectedItem().toString().equalsIgnoreCase("All categories")) {
                        category = "no_category";
                    } else {
                        category = spinner.getSelectedItem().toString();
                    }
                    ActionsForMastByClient mTask = new ActionsForMastByClient(this.getApplicationContext(), act, s, 3, i, new Poi(), j, "10.0.2.2", myGps, category, -700); //for local use
                    //ActionsForMastByClient mTask = new ActionsForMastByClient(this.getApplicationContext(), act, s, 3, i, new Poi(), j, "192.168.1.4", myGps, category, -700);
                    mTask.execute();
                }
            }else{
                open3(txtAsyncResponse);
            }
        }
    }

    public void open(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The learning process is not done, yet. Please try again later.");
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(MainActivity.this,"You clicked Okay button",Toast.LENGTH_LONG).show();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void open2(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please fill out both fields to continue.");
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainActivity.this,"You clicked Okay button",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void open3(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Wrong input! Please enter numbers only.");
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainActivity.this,"You clicked Okay button",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void open4(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("No Pois found for these inputs. Please try again with different values.");
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainActivity.this,"You clicked Okay button",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    public void nextAct(ArrayList<Poi> dt){
        if(dt.get(0).getId()==-100){
            open4(txtAsyncResponse);
        }else {
            Poi temp=new Poi();
            temp.setId(-500);
            temp.setName("You");
            temp.setLatitude(myGps.latitude);
            temp.setLongitude(myGps.longitude);
            dt.add(temp);
            Intent myIntent = new Intent(this, ScrollingActivity.class);
            myIntent.putExtra("PoiList", dt); //Optional parameters
            this.startActivity(myIntent);
        }

    }

}
