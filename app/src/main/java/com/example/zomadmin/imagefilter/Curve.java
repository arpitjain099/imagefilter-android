package com.example.zomadmin.imagefilter;

/**
 * Created by zomadmin on 12/11/14.
 */
public class Curve {

    private int count;
    private int[] x;
    private int[] y;
    private int index=0;

    public Curve(int points_on_curve) {
        this.count = points_on_curve;
        x = new int[points_on_curve];
        y = new int[points_on_curve];
    }

    public void addPoint(int x,int y){
        this.x[index] = x;
        this.y[index++] = y;
    }

    @Override
    public String toString() {
        return count + ": xdata=" + Repr(x) + " ydata=" + Repr(y);
    }

    private String Repr(int[] x2) {
        String r ="";
        for(int i=0;i<count;i++){
            r+= x2[i]+",";
        }
        return r;
    }

    public int[] getX(){
        return x;
    }

    public int[] getY(){
        return y;
    }

}
