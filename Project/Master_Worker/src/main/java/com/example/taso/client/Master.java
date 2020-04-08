package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import javafx.util.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Master {
		private static final int MAX_N_OF_ROUNDS = 5;
		private int N_ofConnections = 0;
		private static int vhma = 200;
		private static int flag=0;
		private static int flag2=0;
		private static int rows=835;
		private static int columns=1692;
		/*private static int rows=765;
		private static int columns=1964;*/
		private static double[][] Data = parse();
		private static double currentError=0;
		private static double previousError;
		private static int round = 1;
		private static int signal=1;
		private static int k=20;
		private static double l=0.1;
		private static int a=40;
		private static RealMatrix p;
		private static RealMatrix c_u_i;
		private static RealMatrix X = MatrixUtils.createRealMatrix(rows,k);
		private static RealMatrix Y = MatrixUtils.createRealMatrix(columns,k);
		private static boolean LearningMode = true;
		private static RealMatrix recommendations=MatrixUtils.createRealMatrix(rows,columns);
		static Pair<Integer, Integer> pair ;
		private static ArrayList<Pair> boundsForX = new ArrayList<Pair>();
		private static ArrayList<Pair> boundsForY = new ArrayList<Pair>();
		private static Poi [] poiMatrix = parsePois();

		public static void main(String args[]) {
			culculateCmatrix(a,c_u_i);
			culculatePmatrix(p);
			initializeBounds();
		    RandomGenerator randomGenerator = new JDKRandomGenerator();
	        randomGenerator.setSeed(1);


	         for(int i=0; i<X.getRowDimension(); i++){  //edw gemizw mono gia thn prwth fora ton X me tyxaies times.
	            for(int j=0; j<X.getColumnDimension(); j++){
	                X.setEntry(i,j,randomGenerator.nextDouble());
	            }
	        }

	        for(int i=0; i<Y.getRowDimension(); i++){ //edw gemizw mono gia thn prwth fora ton Y me tyxaies times.
	            for(int j=0; j<Y.getColumnDimension(); j++){
	                Y.setEntry(i,j,randomGenerator.nextDouble());
	            }
	        }

	        Master maste =new Master();
	        maste.initializeBounds();
	        maste.initialize();
	        
	        
	    }
		
	   
	    /* Define the socket that receives requests */
		ServerSocket providerSocket;
		Socket connection = null;
	    /* Define the socket that is used to handle the connection */
	   
	    void initialize() {
	        try {
	            /* Create Server Socket */
				providerSocket = new ServerSocket(4321,10); // to 2o orisma einai to max clients (edw mexri kai 10) ??? 
	 
	            while (true) {
	                /* Accept the connection */
					connection = providerSocket.accept();
					N_ofConnections+=1;

	                /* Handle the request */
					Thread T = new ActionsForWorkers(connection,N_ofConnections,Data,p,c_u_i,X,Y,LearningMode);
					T.start();
					
	            }
	 
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        } finally {
	            try {
	                providerSocket.close();
	            } catch (IOException ioException) {
	                ioException.printStackTrace();
	            }
	        }
	    }

	static public double[][] parse() {
		String fileInput = "input_matrix_non_zeros.csv";
		//String fileInput = "data.csv";
		double[][] Data = new double[rows][columns];
		File file = new File(fileInput);

		{
			try {

				Scanner input = new Scanner (file);
				input.useDelimiter("[,\n\\s]");

				while(input.hasNext() ) {
					String ele_1 = input.next();
					if( ele_1.length()==0 ) {
						ele_1 = input.next();
					}
					String ele_2 = input.next();
					if(ele_2.length()==0) {
						ele_2 = input.next();
					}
					String ele_3 = input.next();
					if(ele_3.length()==0) {
						ele_3 = input.next();
					}
					
					Data[Integer.parseInt(ele_1)][Integer.parseInt(ele_2)] = Integer.parseInt(ele_3);
				}
				input.close();
			}catch (FileNotFoundException e){
				e.printStackTrace();
			}
		}
		return Data;
	}

	static public Poi[] parsePois() {
		String fileInput = "POIs.json";
		Poi[] Data = new Poi[columns];
		//Poi[] Data = new Poi[1692];
		int i=0;

		try {
			BufferedReader in = new BufferedReader(new FileReader(fileInput));
			String str,str2;
			str = in.readLine();
			PrintWriter writer;
			Poi tempPoi=new Poi();
			while ((str = in.readLine()) != null){
				if(str.startsWith("    \"POI\":" )){
					StringTokenizer st = new StringTokenizer(str,"\"");
					while (st.hasMoreTokens()) {
						if(st.nextToken().startsWith(": ")){
							tempPoi.setPoi(st.nextToken());
						}
					}
					tempPoi.setId(i);

				}
				if(str.startsWith("    \"latidude\"" )){
					StringTokenizer st = new StringTokenizer(str,"\":,");
					while (st.hasMoreTokens()) {
						if(st.nextToken().startsWith("latidude")){
							tempPoi.setLatitude(Double.parseDouble(st.nextToken()));
						}
					}
				}
				if(str.startsWith("    \"longitude\"" )){
					StringTokenizer st = new StringTokenizer(str,"\":,");
					while (st.hasMoreTokens()) {
						if(st.nextToken().startsWith("longitude")){
							str2=st.nextToken();
							str2=str2.replaceAll("\\s+","");
							if(str2.startsWith("-")){
								tempPoi.setLongitude(-1*Double.parseDouble(str2.substring(1)));
							}else{
								tempPoi.setLongitude(Double.parseDouble(str2));
							}

						}
					}
				}
				if(str.startsWith("    \"photos\"" )){
					StringTokenizer st = new StringTokenizer(str,"\"");
					while (st.hasMoreTokens()) {
						if(st.nextToken().startsWith(": ")){
							str2=st.nextToken();
							str2=str2.replaceAll("/+","");
							tempPoi.setPhotos(str2);
						}
					}
				}
				if(str.startsWith("    \"POI_category_id\"" )){
					StringTokenizer st = new StringTokenizer(str,"\"");
					while (st.hasMoreTokens()) {
						if(st.nextToken().startsWith(": ")){
							tempPoi.setCategory(st.nextToken());
						}
					}
				}
				if(str.startsWith("    \"POI_name\"" )){
					StringTokenizer st = new StringTokenizer(str,"\"");
					while (st.hasMoreTokens()) {
						if(st.nextToken().startsWith(": ")){
							tempPoi.setName(st.nextToken());
						}
					}
					Data[i]=tempPoi;
					tempPoi=new Poi();
					i++;
				}
			}
			in.close();
		} catch (IOException e) {
			System.out.println("error in json file");
		}
		return Data;
	}
	
	public static void calculateRec() {
		recommendations=X.multiply(Y.transpose());
	}


	public static void initializeBounds() {
		boundsForX.clear();
		boundsForY.clear();
		int i1 = 0;
		int j1 = vhma-1;
		int x1 = 0;
		int y1 = vhma-1;
		if(j1>(rows-1)){
			pair = new Pair<Integer, Integer>(i1, (rows-1) );
			boundsForX.add(pair);
		}else{
			pair = new Pair<Integer, Integer>(i1, j1);
			boundsForX.add(pair);
		}
		while(j1<(rows-1)){
			i1+=vhma;
			j1+=vhma;
			if(j1>(rows-1)){
				pair = new Pair<Integer, Integer>(i1, (rows-1) );
				boundsForX.add(pair);
			}else{
				pair = new Pair<Integer, Integer>(i1, j1);
				boundsForX.add(pair);
			}
		}
		if(y1>(columns-1)){
			pair = new Pair<Integer, Integer>(x1, (columns-1) );
			boundsForY.add(pair);
		}else{
			pair = new Pair<Integer, Integer>(x1, y1);
			boundsForY.add(pair);
		}
		while(y1<(columns-1)){
			x1+=vhma;
			y1+=vhma;
			if(y1>(columns-1)){
				pair = new Pair<Integer, Integer>(x1, (columns-1) );
				boundsForY.add(pair);
			}else{
				pair = new Pair<Integer, Integer>(x1, y1);
				boundsForY.add(pair);
			}
		}

	}
	void setSignal(int signal){
	    	this.signal=signal;
	}

	static void culculateCmatrix(int a, RealMatrix mt){

		c_u_i = MatrixUtils.createRealMatrix(Data);
		for(int i = 0; i < c_u_i.getRowDimension(); i++) {
			for(int j = 0; j < c_u_i.getColumnDimension(); j++) {
				c_u_i.setEntry(i,j,(c_u_i.getEntry(i,j)*a)+1); //caplulate C matrix
			}
		}
	}

	static void culculatePmatrix(RealMatrix mt){
		p = MatrixUtils.createRealMatrix(Data);
		for(int i = 0; i < p.getRowDimension(); i++) {
			for(int j = 0; j < p.getColumnDimension(); j++) {
				if(p.getEntry(i,j)!=0){
					p.setEntry(i,j,1); //caplulate p matrix
				}

			}
		}
	}

	static void updateX(ObjectWithData x){

		ArrayList<Pair> temp = new ArrayList<Pair>(x.getMyBounds());
		int vhma2 = ( (Integer)temp.get(0).getValue() - (Integer)temp.get(0).getKey() ) + 1 ;
		RealMatrix XFromOBJ=x.getMatrixX();
		for(int i=0; i<temp.size(); i++){
			vhma2=( (Integer)temp.get(i).getValue() - (Integer)temp.get(i).getKey() ) + 1 ;
			int kati=vhma-vhma2;
			for(int j=0; j<vhma2; j++){
				X.setRow(((Integer)temp.get(i).getKey()+j), XFromOBJ.getRow(((j+(i*(vhma2+kati))))) );
			}
		}
		new ActionsForWorkers().CountUpdatepp(temp.size());
		
	}

	static void setVhma(int v){
		vhma=v;
	}
	
	static void updateY(ObjectWithData y){

		ArrayList<Pair> temp = new ArrayList<Pair>(y.getMyBounds());
		int vhma2 = ( (Integer)temp.get(0).getValue() - (Integer)temp.get(0).getKey() ) + 1 ;
		RealMatrix YFromOBJ=y.getMatrixY();
		for(int i=0; i<temp.size(); i++){
			vhma2=( (Integer)temp.get(i).getValue() - (Integer)temp.get(i).getKey() ) + 1 ; //ALlagh!!!!!!!!!!!!!!!!!!!!!!
			int kati=vhma-vhma2;
			for(int j=0; j<vhma2; j++){  //provlhma gia thn teleytaia epanalhpsh pou exei ligotera apo 100 to vhma
				Y.setRow(((Integer)temp.get(i).getKey()+j), YFromOBJ.getRow(((j+(i*(vhma2+kati))))) );
			}
		}
		new ActionsForWorkers().CountUpdatepp(temp.size()); //ftiakste ayto na metraei swsta (nomizw to eytiaksa)
	}

	public static double caltulateError(RealMatrix p, RealMatrix c_u_i, RealMatrix X, RealMatrix Y){
        double FirstPart = 0;
        double FinalPart = 0;
        double frbForX=0;
        double frbForY=0;
        double temp=0;
        for(int i=0 ; i<X.getRowDimension(); i++){
            temp=X.getRowMatrix(i).getFrobeniusNorm();
            frbForX+=temp*temp;
            for(int j=0 ; j<Y.getRowDimension(); j++){
                if(i==0){
                    temp=Y.getRowMatrix(j).getFrobeniusNorm();
                    frbForY+=temp*temp;
                }
                RealMatrix MultResult = (X.getRowMatrix(i).multiply(Y.getRowMatrix(j).transpose()));
                FirstPart+= c_u_i.getEntry(i,j)* ( (p.getEntry(i,j)-MultResult.getEntry(0,0)) * (p.getEntry(i,j)-MultResult.getEntry(0,0)) ) ;
            }

        }
        FinalPart = FirstPart + ( l * (frbForX + frbForY) ) ;
        return FinalPart;
    }
	
	static void signalForError(){
		round++;
		flag=0;
		flag2=0;
		previousError = currentError;
		currentError = caltulateError(p, c_u_i, X, Y);
		System.out.println("to currentError einai: "+currentError);
		if(round > MAX_N_OF_ROUNDS || Math.abs(previousError-currentError)<=0.01 ){
			System.out.println("H ekpedeysh exei teliwsei, eginan "+(round-1)+" gyros/oi, to trexon sfalma einai: "+currentError+" kai h diafora me to proigoumeno sfalma einai: "+Math.abs(previousError-currentError));
			LearningMode=false;
			calculateRec();

		}else{
			initializeBounds(); //Arxikopoiw ta bounds gia ton epomeno guro
			System.out.println("Ksekinaw ton guro ekpedeushs: "+round);
		}

	}

	public static Poi[] getPoiMatrix(){
		return poiMatrix;
	}

	public static void setPoiMatrix(Poi[] poiMatrix) {
		Master.poiMatrix = poiMatrix;
	}

	public static double getL(){
	    	return l;
	}

	public static int getRows() {
		return rows;
	}

	public static int getColumns() {
		return columns;
	}

	public static int getK(){
	    	return k;
	}

	public static int getVhma(){
		return vhma;
	}

	public static RealMatrix getX (){
		return X;
	}

	public Pair<Integer,Integer> getNextBoundX(){
	    	if(boundsForX.size()==0){
				return new Pair<Integer, Integer>(-111,-111);
			}else {
				return boundsForX.remove(0);
			}
	}

	public Pair<Integer,Integer> getNextBoundY(){
		if(boundsForY.size()==0){
			return new Pair<Integer, Integer>(-111,-111);
		}else {
			return boundsForY.remove(0);
		}
	}

	public int getSizeOfBoundX(){
	    	return boundsForX.size();
	}

	public int getSizeOfBoundY(){
		return boundsForY.size();
	}

	public static ArrayList<Pair> getBoundsForX() {
		return boundsForX;
	}

	public static ArrayList<Pair> getBoundsForY() {
		return boundsForY;
	}

	public static RealMatrix getY (){
		return Y;
	}

	public static RealMatrix getRec(int id) {
		
		return recommendations.getRowMatrix(id);	
		}
}
