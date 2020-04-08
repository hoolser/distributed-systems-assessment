package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import android.content.Context;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class ActionsForMastByClient extends AsyncTask<Void, Void, String> {

    int signal;
    ObjectInputStream in;
    ObjectOutputStream out;
    private Socket requestSocket;
    private int id;
    private Poi poi;
    private int NbestPois;
    ObjectClient obj;
    ObjectToReturn whatiget;
    private Context context;
    private String IP;
    private ArrayList<Poi> pois = new ArrayList<Poi>();
    private LatLng myGps;
    private String category;
    private int range;
    OnDownloadUpdateListener listener;

    public interface OnDownloadUpdateListener {
        public void OnDownloadDeckFinish(ArrayList<Poi> Response);
    }



    public ActionsForMastByClient(Context ctx,OnDownloadUpdateListener listener,Socket requestSocket, int i,int id, Poi poi, int N, String IP, LatLng la,String category,int range) {
        this.context = ctx;
        this.listener = listener;
        this.requestSocket = requestSocket;
        this.signal=i;
        this.id=id;
        this.poi=poi;
        this.NbestPois=N;
        this.myGps=la;
        this.IP=IP;
        this.category=category;
        this.range=range;
        obj=new ObjectClient(this.id,this.poi,this.NbestPois,this.range,this.category,this.myGps.latitude,this.myGps.longitude);
    }

    @Override
    protected String doInBackground(Void... params) {
        //your async code goes here
        try {
            requestSocket = new Socket(IP, 4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in = new ObjectInputStream(requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out.writeInt(signal);
            out.flush();
        } catch (IOException e) {

        }
        if(signal==3){
            try {
                out.writeObject(new ObjectClient(obj));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                whatiget=new ObjectToReturn ((ObjectToReturn) in.readObject());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(whatiget.getPoiList().size()>0) {
                if (whatiget.getPoiList().get(0).getId() == -1) {
                    return "training is not done!";
                } else {
                    pois = new ArrayList<Poi>(whatiget.getPoiList());
                }
            }else{
                Poi temp=new Poi();
                temp.setId(-100);
                pois.add(temp);
            }

        }
        return "I get the Pois!";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if( listener != null ) {
            listener.OnDownloadDeckFinish(pois);
        }
    }

}





