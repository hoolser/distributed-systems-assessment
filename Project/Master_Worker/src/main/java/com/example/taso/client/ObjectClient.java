package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import java.io.Serializable;

public class ObjectClient implements Serializable {
    private int id;
    private Poi poi;
    private int NbestPois;
    private int kill;
    private int range;
    private String category;
    private double Lat;
    private double Lng;
    private static long sID= -1L;

    ObjectClient(){

    }

    ObjectClient(ObjectClient o){
        this.id=o.getId();
        this.poi=o.getPoi();
        this.NbestPois=o.getNbestPois();
        this.kill=o.getKill();
        this.range=o.getRange();
        this.category=o.getCategory();
        this.Lat=o.getLat();
        this.Lng=o.getLng();
    }

    ObjectClient(int id, Poi poi, int N,int range, String category,double Lat,double Lng ){
        this.id=id;
        this.poi=poi;
        this.NbestPois=N;
        this.range=range;
        this.category=category;
        this.Lat=Lat;
        this.Lng=Lng;
    }

    public int getId() {
        return id;
    }

    public int getNbestPois() {
        return NbestPois;
    }

    public Poi getPoi() {
        return poi;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }

    public void setNbestPois(int nbestPois) {
        this.NbestPois = nbestPois;
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

    public int getRange() {return range;}

    public void setRange(int range) {this.range=range;}

    public String getCategory() {return category;}

    public void setCategory(String category) {this.category=category;}

    public double getLat() {return Lat;}

    public void setLat(double Lat) {this.Lat=Lat;}

    public double getLng() {return Lng;}

    public void setLng(double Lgn) {this.Lng=Lng;}

}
