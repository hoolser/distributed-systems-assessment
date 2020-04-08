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
import java.util.concurrent.TimeUnit;


public class Worker1 {
    private static int k=20;
    private static double l=0.1;
    private static int rows=835;
    private static int columns=1692;
    /*private static int rows=765;
    private static int columns=1964;*/
    static double [][] matrixdata = {{1d,2d,3d},{2d,5d,3d}};
    static RealMatrix matrix = MatrixUtils.createRealMatrix(matrixdata);
    static ObjectWithData obj =new ObjectWithData(matrix,matrix);
    private static RealMatrix p;
    private static RealMatrix c_u_i;
    private static RealMatrix X = MatrixUtils.createRealMatrix(rows,k);
    private static RealMatrix Y = MatrixUtils.createRealMatrix(columns,k);
    private static int kill;
    private static boolean flag=true;
    private static boolean flag2=false;
    private static boolean waitFor_XorY=true;
    static Pair<Integer, Integer> pair ;
    private static ArrayList<Pair> MyBounds = new ArrayList<Pair>();


    Worker1() {
    }

    static Socket requestSocket = null;

    public void initialize(int i) {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("localhost", 4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread client = new ActionsForMaster(requestSocket,i,obj);
        client.start();
    }

    public void UpdateFlag(){
        setFlag(true);
    }

    public void UpdateKill(int kill){
        this.kill=kill;
    }

    public int getKill(){
        return this.kill;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void updateC_P(ObjectWithData obj){
        c_u_i = MatrixUtils.createRealMatrix(obj.getMatrixC().getData());
        p = MatrixUtils.createRealMatrix(obj.getMatrixP().getData());
    }

    public RealMatrix getMatrixX() {
        return X;
    }

    public RealMatrix getMatrixY() {
        return Y;
    }

    public static RealMatrix getC_u_i() {
        return c_u_i;
    }

    public static RealMatrix getP() {
        return p;
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

    public void add_MyBounds(int i1, int j1){
        pair = new Pair<Integer, Integer>(i1, j1 );
        MyBounds.add(pair);
    }

    public ArrayList<Pair> getMyBounds(){return MyBounds;}

    public void clearMyBounds() {
        MyBounds.clear();
    }

    public static void setX(RealMatrix x) {
        X = x;
    }

    public static void setY(RealMatrix y) {
        Y = y;
    }

    public void setWaitforXorY(boolean tr){
        waitFor_XorY=tr;
    }

    public static boolean isWaitFor_XorY() {
        return waitFor_XorY;
    }

    public static boolean isFlag2() {
        return flag2;
    }

    public static void setFlag2(boolean flag2) {
        Worker1.flag2 = flag2;
    }

    public static void main(String args[]) throws InterruptedException {
        Worker1 w = new Worker1();
        w.initialize(4);
        while(c_u_i==null && p==null){
            TimeUnit.SECONDS.sleep(5);
        }
        w.initialize(5);
        while(waitFor_XorY){
            TimeUnit.SECONDS.sleep(4);
        }
        w.setWaitforXorY(true);
        do {
            if(!flag2){
                if(w.getFlag()){
                    w.setFlag(false);
                    w.initialize(1);
                }
            }else{
                w.initialize(5);
                while(waitFor_XorY){
                    TimeUnit.SECONDS.sleep(4);
                }
                w.setWaitforXorY(true);
            }
            TimeUnit.SECONDS.sleep(10);
        }while (w.getKill()!=2);
    }

}