package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import javafx.util.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import java.io.Serializable;
import java.util.ArrayList;

public class ObjectWithData implements Serializable{
	
	private RealMatrix X;
	
	private RealMatrix Y;
	private int i;
	private int j;
	private RealMatrix p;
	private RealMatrix c_u_i;
	private int flag;
	private int kill=0;
	private ArrayList<Pair> MyBounds;
	ObjectWithData(RealMatrix p, RealMatrix d){
		X = p;
		Y=  d;
	}
	ObjectWithData(ObjectWithData dataob){
		if(dataob.getMatrixX()!=null){
			this.X=MatrixUtils.createRealMatrix(dataob.getMatrixX().getData());
		}
		if(dataob.getMatrixY()!=null){
			this.Y=MatrixUtils.createRealMatrix(dataob.getMatrixY().getData());
		}
		if(dataob.getMatrixC()!=null){
			this.c_u_i=MatrixUtils.createRealMatrix(dataob.getMatrixC().getData());
		}
		if(dataob.getMatrixP()!=null){
			this.p=MatrixUtils.createRealMatrix(dataob.getMatrixP().getData());
		}
		this.i=dataob.getI();
		this.j=dataob.getJ();
		this.flag=dataob.getflag();
	}

	ObjectWithData(){}


	ObjectWithData(int kill){
		this.kill=kill;
	}
	
	public RealMatrix getMatrixX() {
		return X;
	}

	public RealMatrix getMatrixY() {
		return Y;
	}

	public void setI(int i) {
		this.i = i;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}
	public RealMatrix getMatrixP() {
		return p;
	}
	public RealMatrix getMatrixC() {
		return c_u_i;
		
	}
	public void SetMatrixP(RealMatrix p) {
		this.p=p;
	}
	public void SetMatrixC(RealMatrix c) {
		this.c_u_i=c;
	}
	public void setflag(int f) {
		this.flag = f;
	}
	public int getflag() {
		return this.flag;
	}
	public void SetMatrixX(RealMatrix X) {
		this.X=X;
	}
	public void SetMatrixY(RealMatrix Y) {
		this.Y=Y;
	}
	public void SetRowX(int i,double[] p ) {
		X.setRow(i, p);
	}
	public void SetRowY(int i,double[] p ) {
		Y.setRow(i, p);
	}

	public int getKill() {
		return kill;
	}

	public void setKill(int kill) {
		this.kill = kill;
	}

	public ArrayList<Pair> getMyBounds() {
		return MyBounds;
	}

	public void setMyBounds(ArrayList<Pair> myBounds) {
		MyBounds = myBounds;
	}
}
