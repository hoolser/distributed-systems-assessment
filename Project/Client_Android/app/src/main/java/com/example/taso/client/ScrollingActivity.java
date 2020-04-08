package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    RecyclerView rv;
    ProgressBar progress;
    LinearLayoutManager llm = new LinearLayoutManager(this);
    private List<Poi> data= new ArrayList<>();
    private ArrayList<String> links=new ArrayList<String>();
    private Poi temp;
    adapter adapt;
    private Image_Downloader.OnDownloadUpdateListener act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progress = (ProgressBar)findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        data =(ArrayList<Poi>)getIntent().getSerializableExtra("PoiList");
        temp=data.remove(data.size()-1);
        for(int i=0 ; i<data.size(); i++){
            links.add(data.get(i).getPhotos());
        }
        act = new Image_Downloader.OnDownloadUpdateListener() {
            @Override
            public void OnDownloadDeckFinish(ArrayList<Bitmap> Response) {
                progress.setVisibility(View.GONE);
                rv = (RecyclerView)findViewById(R.id.rv);
                rv.setLayoutManager(llm);
                adapt = new adapter(data,Response);
                rv.setAdapter(adapt);
            }
        };
        Image_Downloader mTask = new Image_Downloader(this.getApplicationContext(), act,links);
        mTask.execute();
    }

    public void action(View view){
        nextAct(new ArrayList<Poi>(data));
    }

    public void nextAct(ArrayList<Poi> dt){
        Intent myIntent = new Intent(this, MapsActivity.class);
        dt.add(temp);
        myIntent.putExtra("PoiList2", dt); //Optional parameters
        this.startActivity(myIntent);
    }
}
