package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import javafx.util.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ActionsForWorkers extends Thread{

    private static int k;
    private static double l;
    private static int rows;
    private static int columns;
    private static int vhma;
    private double[][] Data ;
    private static RealMatrix X;
    private static   RealMatrix Y;
    private RealMatrix p;
    private RealMatrix c_u_i;
    ObjectInputStream in;
    ObjectOutputStream out;
    private double [][] matrixdata = {{1d,2d,3d},{2d,5d,3d}};
    private RealMatrix matrix = MatrixUtils.createRealMatrix(matrixdata);
    private int N_ofConnections;
    private int signal;
    private boolean LearningMode;
    private static boolean Anamonh=false;
    private static int countDistribute=0;
    private static int countUpdate=0;
    private static  ObjectClient objC;
    private static boolean flag5=false;
    private Master mast=new Master();

    private ObjectWithData obj = new ObjectWithData(matrix,matrix);

    public ActionsForWorkers(Socket connection ,int N_ofConnections, double[][] data, RealMatrix p, RealMatrix c_u_i,RealMatrix X, RealMatrix Y,boolean LearningMode) {
        Master temp = new Master();
        this.k=temp.getK();
        this.l=temp.getL();
        this.rows=temp.getRows();
        this.columns=temp.getColumns();
        this.vhma=temp.getVhma();
        this.X = MatrixUtils.createRealMatrix(rows,k);
        this.Y = MatrixUtils.createRealMatrix(columns,k);
       
        this.LearningMode=LearningMode;
        this.p=p;
        this.c_u_i=c_u_i;
        Data=data;
        this.N_ofConnections=N_ofConnections;
        this.X=X;
        this.Y=Y;
        
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ActionsForWorkers(){

    }

    public void run() {
        try {
            signal = in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Master().setSignal(signal);
        if(signal==1) {
            if(!LearningMode){
                obj = new ObjectWithData(2);
                try {
                    out.writeObject(obj);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(Anamonh){
                obj = new ObjectWithData(1);
                try {
                    out.writeObject(obj);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if(mast.getSizeOfBoundY()==0 && mast.getSizeOfBoundX()==0){//Exei teliwsei h ekpedeysh!
                    obj = new ObjectWithData(1);
                    try {
                        out.writeObject(obj);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(flag5==false){
                        flag5=true;
                        mast.signalForError();
                    }
                } else if ( mast.getSizeOfBoundX()==0 ) {
                    Pair<Integer,Integer> testpair = mast.getNextBoundY();
                    System.out.println("moirazw ta bounds " +testpair.getKey()+","+testpair.getValue()+" tou pinaka Y");
                    distributeYMatrixToWorkers(testpair.getKey(), testpair.getValue());
                } else {
                    Pair<Integer,Integer> testpair = mast.getNextBoundX();
                    System.out.println("moirazw ta bounds " +testpair.getKey()+","+testpair.getValue()+" tou pinaka X");
                    distributeXMatrixToWorkers(testpair.getKey(), testpair.getValue());
                }
            }
        }else if(signal==2){
            mast.setSignal(signal);
            try {
                obj = (ObjectWithData) in.readObject();
             } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("Elava pinaka apo ton worker kai kanw ananewsh.");
            if(obj.getflag()==0){
                mast.updateX(obj);
            }else{
                mast.updateY(obj);
            }

        }else if(signal==3){ /*edw stelnei o client gia na parei recommendation*/
            new Master().setSignal(signal);
            if(LearningMode){//an eimaste akoma se learning mode o client prepei na perimenei
            	try {
                    objC = (ObjectClient) in.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            	List<Poi> best= new ArrayList<Poi>();
            	Poi temp= new Poi();
            	temp.setId(-1);
            	best.add(temp);
            	ObjectToReturn returnme= new ObjectToReturn(best);
            	try {
					out.writeObject(returnme);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }else {//edw eksupiretitai kanonika o client
            	try {
                    objC = (ObjectClient) in.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            	List<Poi> best;
            	best=calculateBestLocalPoisForUser(objC.getNbestPois(),objC.getLat(),objC.getLng(),objC.getId(),objC.getRange(),objC.getCategory());
            	ObjectToReturn returnme= new ObjectToReturn(best);
            	try {
					out.writeObject(returnme);
					out.flush();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
            }
        }else if(signal == 4){ //edw enas worker sindeete prwth fora
            obj= new ObjectWithData();
            obj.SetMatrixC(c_u_i);
            obj.SetMatrixP(p);
            try {
                out.writeObject(new ObjectWithData(obj));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(signal == 5){ //dinw ton X h ton Y analoga
            if(!LearningMode){
                obj = new ObjectWithData(2);
                try {
                    out.writeObject(obj);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(Anamonh){
                obj = new ObjectWithData(1);
                try {
                    out.writeObject(obj);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                obj= new ObjectWithData();
                if(mast.getBoundsForX().size()!=0){
                   //stelnw Y
                   obj.setflag(1);
                   obj.SetMatrixY(mast.getY());
                    try {
                        out.writeObject(new ObjectWithData(obj));
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
               }else if(mast.getBoundsForY().size()!=0){
                    //stelnw X
                    obj.setflag(0);
                    obj.SetMatrixX(mast.getX());
                    try {
                        out.writeObject(new ObjectWithData(obj));
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    if(mast.getBoundsForX().size()==0 && mast.getBoundsForY().size()==0){
                        if(flag5==false){
                            flag5=true;
                            mast.signalForError();
                        }
                    }
                    obj = new ObjectWithData(1);
                    try {
                        out.writeObject(obj);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
               
    }
    
    public   List<Poi> calculateBestLocalPoisForUser(int npois,double lat ,double lng,int id,int range,String category){
    	ArrayList<ArrayList<Double>> BL=new ArrayList<ArrayList<Double>>();
    	ArrayList<Double> templist= new ArrayList<Double>();
    	List<Poi> Poislist=new ArrayList<Poi>();
    	Poi temp;
    	Poi[] realPois = mast.getPoiMatrix(); //edw pernoume ta Pois apo ton Master
    	RealMatrix kat=new Master().getRec(id);
    	for(int i=0;i<kat.getColumnDimension();i++) {
    		templist.add((double) kat.getEntry(0,i));
    		templist.add((double) i);
    		BL.add(new ArrayList<Double>(templist));
    		templist.clear();
    	}
    	Collections.sort(BL, new Comparator<ArrayList<Double>> () {
    	    @Override
    	    public int compare(ArrayList<Double> a, ArrayList<Double> b) {
    	        return a.get(0).compareTo(b.get(0));
    	    }
    	});
        int tempID;
    	for(int i=0;i<npois;i++) {
    	    if(BL.size()>0) {
                tempID = (BL.remove(BL.size() - 1).remove(1)).intValue();
            }else{
    	        break;
            }
    		if(p.getEntry(id,tempID)!=1){ //ayto einai elegxos gia to an exei paei o xrhsths sto Poi
                for (int j = 0; j < realPois.length; j++) {
                    if (realPois[j].getId() == tempID) {
                        if(realPois[j].getCategory().equalsIgnoreCase(category) || category.equalsIgnoreCase("no_category")){ //ayto einai elegxos gia to an to Poi einai sto categoy
                            if( ( distance( lat, lng, realPois[j].getLatitude(),  realPois[j].getLongitude(),  "K")  <= (range+0.0) ) || range==-700 ) { //ayto einai elegxos gia to an to Poi einai sto range ths topothesias tou xrhsth.
                                Poislist.add(realPois[j]);
                            }else{
                                i--;
                            }
                        }else{
                            i--;
                        }
                    }
                }
            }else{
    		    i--;
            }
    	}
		return Poislist;
    }

    public void distributeXMatrixToWorkers(int a , int b ) {
        try {
            countDistribute++;
            if((((rows%vhma)==0)?(rows/vhma):((rows/vhma)+1))==countDistribute){
                Anamonh=true;
            }
            obj= new ObjectWithData();
            obj.setI(a);
            obj.setJ(b);
            obj.setflag(0);
            out.writeObject(new ObjectWithData(obj));
            out.flush();
         
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void distributeYMatrixToWorkers(int a , int b ) {
        try {
            countDistribute++;
            if((((columns%vhma)==0)?(columns/vhma):((columns/vhma)+1))==countDistribute){
                Anamonh=true;
            }
            obj= new ObjectWithData();
            obj.setflag(1);
            obj.setI(a);
            obj.setJ(b);
            out.writeObject(new ObjectWithData(obj));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void CountUpdatepp(int N){
        countUpdate+=N;
        if(Anamonh && (countUpdate==countDistribute)){
            Anamonh=false;
            flag5=false;
            countUpdate=0;
            countDistribute=0;
        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }
        return (dist);
    }
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}