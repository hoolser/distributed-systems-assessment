package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class Image_Downloader extends AsyncTask<Void, Void, String> {
    private Context context;
    private ArrayList<String> links;
    ArrayList<Bitmap> mybitmap=new ArrayList<Bitmap>();
    OnDownloadUpdateListener listener;

    public interface OnDownloadUpdateListener {
        public void OnDownloadDeckFinish(ArrayList<Bitmap> Response);
    }

    public Image_Downloader(Context ctx,OnDownloadUpdateListener listener,ArrayList<String> links) {
        this.context = ctx;
        this.listener = listener;
        this.links=links;
    }

    @Override
    protected String doInBackground(Void... params) {
        for(int i=0; i<links.size() ; i++) {
            if(!links.get(i).equalsIgnoreCase("Not exists")) {
                try {
                    URL url = new URL(links.get(i));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    try{
                        InputStream input = connection.getInputStream();
                        mybitmap.add(BitmapFactory.decodeStream(input));
                    }catch (FileNotFoundException e){
                        mybitmap.add(null);
                    }
                    catch(Exception e){
                        mybitmap.add(null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                mybitmap.add(null);
            }
        }
        return "I am done!";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if( listener != null ) {
            listener.OnDownloadDeckFinish(mybitmap);
            System.out.println("nphka edw 33");
        }
    }
}