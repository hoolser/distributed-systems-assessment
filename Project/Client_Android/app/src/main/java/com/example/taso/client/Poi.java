package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import java.io.Serializable;

public class Poi implements Serializable {
	private int id;
	private String poi;
	private String name;
	private String category;
	private double latitude;
	private double longitude;
	private String photos;

	public String getPoi(){
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public String toString(){
		return ("name:"+name+" , category"+ category+" , id"+ id+" , photoLink"+ photos);
	}

}
