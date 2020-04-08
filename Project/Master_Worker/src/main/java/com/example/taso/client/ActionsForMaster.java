package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import javafx.util.Pair;
import org.apache.commons.math3.linear.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ActionsForMaster extends Thread{
    private static int k=20;
    private static double l=0.1;
    int signal;
    ObjectInputStream in;
    ObjectOutputStream out;
    private Socket requestSocket;
    double [][] matrixdata = {{123d,122},{2d,5d}};
    RealMatrix matrix = MatrixUtils.createRealMatrix(matrixdata);
    ObjectWithData obj=new ObjectWithData(matrix,matrix);
    private static boolean flag4=false;
    Worker1 w = new Worker1();
    private static boolean I_Workered=false;
    private static RealMatrix X ;
    private static RealMatrix Y ;
    private static RealMatrix p;
    private static RealMatrix c_u_i;
    private static int Realvhma=200;

    ActionsForMaster(Socket requestSocket, int i, ObjectWithData obj){
        this.requestSocket = requestSocket;
        signal=i;
        this.obj=obj;
    }

    public void run() {

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

        if (signal == 1) {
            try {
                obj = (ObjectWithData) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (obj.getKill() == 1) {
                if (!flag4) {
                    System.out.println("Perimenw na ypologistei o trexon pinakas, prin parw douleia.");
                    if(I_Workered){
                        w.initialize(2);
                        I_Workered=false;
                    }else{
                        w.clearMyBounds();
                    }
                }
                flag4 = true;
                w.setFlag(true);
                w.setFlag2(true);
                return;
            } else if (obj.getKill() == 2) {
                System.out.println("Exei teliwsei h ekpedeush!");
                w.UpdateKill(obj.getKill());
                return;
            }
            I_Workered=true;
            System.out.println("Exw parei ta bounds " + obj.getI() + " mexri " + obj.getJ()+  " tou pinaka " + ((obj.getflag() == 0) ? "X" : "Y")  + " kai kanw ypologismous.");
            w.add_MyBounds(obj.getI(), obj.getJ());
            flag4 = false;
            p=w.getP();
            c_u_i=w.getC_u_i();
            if (obj.getflag() == 0) {
                Y=w.getMatrixY();
                for (int i = obj.getI(); i <= obj.getJ(); i++) {
                    w.SetRowX(i, calculate_x_u(i, Y, c_u_i, p).getColumn(0));
                }
            } else {
                X=w.getMatrixX();
                for (int i = obj.getI(); i <= obj.getJ(); i++) {
                    w.SetRowY(i, calculate_y_i(i, X, c_u_i, p).getColumn(0));
                }
            }
            w.UpdateFlag();
        } else if (signal == 4) {//request c_u_i , p
            try {
                obj = (ObjectWithData) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            w.updateC_P(obj);
        }else if(signal == 5){ //pernw h ton X h ton Y analoga me to ti xreiazetai (thelei douleia akoma)
            try {
                obj = (ObjectWithData) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (obj.getKill() == 1) {
                if (!flag4) {
                    System.out.println("Perimenw na ypologistei o trexon pinakas, prin parw douleia.");
                }if(!I_Workered){
                    w.clearMyBounds();
                }
                flag4 = true;
                w.setFlag(true);
                w.setWaitforXorY(false);
                return;
            } else if (obj.getKill() == 2) {
                System.out.println("Exei teliwsei h ekpedeush!");
                w.UpdateKill(obj.getKill());
                w.setWaitforXorY(false);
                return;
            }
            w.setFlag2(false);
            flag4=false;
            System.out.println("Exw parei ton pinaka " + ((obj.getflag() == 0) ? "X" : "Y") );
            w.setWaitforXorY(false);
            if(obj.getflag()==0){
                w.setX(obj.getMatrixX());
                w.add_MyBounds(-2,-2); //otan pernw ton X argotera tha steilw ton Y
            }else{
                w.setY(obj.getMatrixY());
                w.add_MyBounds(-1,-1); //otan pernw ton Y argotera tha steilw ton X
            }


        }else if(signal == 2){//send results back to Master
            flag4 = true; //Proswxh allaghh mporei na mhn xreiazetai
            sendResultsToMaster();
        }
    }

    public void sendResultsToMaster(){
        try {
            ArrayList<Pair> temp = new ArrayList<Pair>(w.getMyBounds());
            RealMatrix matrix;
            int vhma=0;
            for(int de=1; de<temp.size(); de++){
                vhma+=(( (Integer)temp.get(de).getValue() - (Integer)temp.get(de).getKey() ) +1);
            }
            obj=new ObjectWithData();
            if((Integer)temp.get(0).getKey()==-1){
                RealMatrix X=w.getMatrixX();
                obj.setflag(0);
                temp.remove(0);
                matrix = MatrixUtils.createRealMatrix( vhma,k);
                for(int i=0; i<temp.size(); i++){
                    vhma=( (Integer)temp.get(i).getValue() - (Integer)temp.get(i).getKey() ) + 1 ;
                    int kati=Realvhma-vhma;
                    for(int j=0; j<vhma; j++){
                        matrix.setRow((j+(i*(kati+vhma))), X.getRow(((Integer)temp.get(i).getKey()+j)) );
                    }
                }
                obj.SetMatrixX(matrix);
            }else{
                RealMatrix Y=w.getMatrixY();
                obj.setflag(1);
                temp.remove(0);
                matrix = MatrixUtils.createRealMatrix( vhma,k);
                for(int i=0; i<temp.size(); i++){
                    vhma=( (Integer)temp.get(i).getValue() - (Integer)temp.get(i).getKey() ) + 1 ;
                    int kati=Realvhma-vhma;
                    for(int j=0; j<vhma; j++){
                        matrix.setRow((j+(i*(kati+vhma))), Y.getRow(((Integer)temp.get(i).getKey()+j)) );
                    }
                }
                obj.SetMatrixY(matrix);
            }
            obj.setMyBounds(new ArrayList<Pair>(temp));
            w.clearMyBounds();
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RealMatrix makeMatrixFromVector(RealVector vec){
        RealMatrix p_u = MatrixUtils.createRealMatrix(vec.getDimension(),1);
        for(int i=0; i<vec.getDimension(); i++){
            p_u.setEntry(i,0,vec.getEntry(i));
        }
        return p_u;
    }

    public static RealMatrix calculate_x_u(int u, RealMatrix Y,RealMatrix c_u_i,RealMatrix p){
        RealMatrix Xu= MatrixUtils.createRealMatrix(1,k);
        RealMatrix TransY = Y.transpose();
        DiagonalMatrix C_user=new DiagonalMatrix(c_u_i.getRow(u));
        RealMatrix identity_p = MatrixUtils.createRealIdentityMatrix(Y.getRowDimension());
        RealMatrix identity_k = MatrixUtils.createRealIdentityMatrix(k);
        RealMatrix part1_Inverse = new LUDecomposition(((TransY.multiply(Y)).add((TransY.multiply(C_user.subtract(identity_p)).multiply(Y)))).add(identity_k.scalarMultiply(l))).getSolver().getInverse();
        RealVector p_u = MatrixUtils.createRealVector(p.getRow(u));
        Xu = part1_Inverse.multiply(TransY).multiply(C_user).multiply(makeMatrixFromVector(p_u)) ; //kanw to teleytaio mulptiply
        return Xu;
    }

    public static RealMatrix calculate_y_i(int i, RealMatrix X,RealMatrix c_u_i,RealMatrix p){
        RealMatrix Yi= MatrixUtils.createRealMatrix(1,k);
        RealMatrix TransX = X.transpose();
        DiagonalMatrix C_poi=new DiagonalMatrix(c_u_i.getColumn(i));
        RealMatrix identity_u = MatrixUtils.createRealIdentityMatrix(X.getRowDimension());
        RealMatrix identity_k = MatrixUtils.createRealIdentityMatrix(k);
        RealMatrix part1_Inverse = new LUDecomposition(((TransX.multiply(X)).add((TransX.multiply(C_poi.subtract(identity_u)).multiply(X)))).add(identity_k.scalarMultiply(l))).getSolver().getInverse();
        RealVector p_i = MatrixUtils.createRealVector(p.getColumn(i));
        Yi = part1_Inverse.multiply(TransX).multiply(C_poi).multiply(makeMatrixFromVector(p_i)) ; //kanw to teleytaio mulptiply
        return Yi;
    }

}
