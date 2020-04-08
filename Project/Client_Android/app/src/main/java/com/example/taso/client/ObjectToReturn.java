package com.example.taso.client;
// /Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ObjectToReturn implements Serializable {
	private  List<Poi> poilist;
	private static long sID= -1L;
	  
	  ObjectToReturn(List<Poi> poilist){
		  this.poilist=new ArrayList<Poi>(poilist);
	  }
	  ObjectToReturn(ObjectToReturn anoject){
		  this.poilist=anoject.getPoiList();
	  }
   
	 public List<Poi> getPoiList(){
		 return new ArrayList<Poi>(poilist);
	 }
}
