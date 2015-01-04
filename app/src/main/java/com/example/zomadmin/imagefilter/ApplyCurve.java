package com.example.zomadmin.imagefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Jama.Matrix;

/**
 * Created by zomadmin on 12/11/14.
 */
public class ApplyCurve {

    private String curveFile;
    private Context context;
    private ArrayList<double[]> polynomials;

    public ApplyCurve(String curveFile,Context context)throws IOException{
        this.curveFile = curveFile;
        this.context = context;
        obtainPolys();
    }

    private void obtainPolys()throws IOException{
        InputStream pfile = context.getResources().openRawResource(R.raw.country);
        byte[] polyFile = IOUtils.toByteArray(pfile);
        int version = ((polyFile[0] & 0xff) << 8) | (polyFile[1] & 0xff);
        int curveCount = ((polyFile[2] & 0xff) << 8) | (polyFile[3] & 0xff);
        //System.out.println(version+" "+curveCount);
        ArrayList<Curve> curves = new ArrayList<Curve>();
        int byteIndex = 4;
        for(int i=0;i<curveCount;i++){
            int points_on_curve = ((polyFile[byteIndex++] & 0xff) << 8) | (polyFile[byteIndex++] & 0xff);
            Curve curve = new Curve(points_on_curve);
            for(int j=0;j<points_on_curve;j++){
                int y1 = ((polyFile[byteIndex++] & 0xff) << 8) | (polyFile[byteIndex++] & 0xff);
                int x1 = ((polyFile[byteIndex++] & 0xff) << 8) | (polyFile[byteIndex++] & 0xff);
                //System.out.println(""+x1+" "+y1);
                curve.addPoint(x1, y1);
            }
            curves.add(curve);
        }
        //System.out.println(curves);
        //get the polynomial using lagrange
        polynomials = new ArrayList<double[]>();
        for(Curve curve:curves){
            double[] data = LagrangeInterpolation(curve.getX(),curve.getY());
            for(int i=0;i<data.length;i++){
                System.out.print(data[i]+" ");
            }
            System.out.println();
            polynomials.add(data);
        }
    }

    public Bitmap getModifiedImage(Bitmap orignalImage){
        //Weve got the polynomial now apply it on image
        Bitmap newImage = applyPolyToImage(orignalImage,polynomials);
        return newImage;
    }

    private static double[] LagrangeInterpolation(int x[],int y[]){
        int n = x.length;
        double[][] data = new double[n][n];
        double[]   rhs  = new double[n];

        for (int i = 0; i < n; i++) {
            double v = 1;
            for (int j = 0; j < n; j++) {
                data[i][n-j-1] = v;
                v *= x[i];
            }
            rhs[i] = y[i];
        }

        // Solve m * s = b
        Matrix m = new Matrix (data);
        Matrix b = new Matrix (rhs, n);
        Matrix s = m.solve (b);
        return s.getRowPackedCopy();
    }

    private static int[][] convertTo2DWithoutUsingGetRGB(Bitmap image) {

        final int width = image.getWidth();
        final int height = image.getHeight();

        int[][] result = new int[height][width];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                result[i][j] =image.getPixel(j,i);
            }
        }
        return result;
    }

    private static Bitmap applyPolyToImage(Bitmap image,ArrayList<double[]> poly){
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] intImage = convertTo2DWithoutUsingGetRGB(image);
        Bitmap newImage = Bitmap.createBitmap(image);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int rval = (intImage[i][j] >> 16) & 0xff;
                int gval = (intImage[i][j] >> 8) & 0xff;
                int bval = intImage[i][j] & 0xff;
                //int cval = (intImage[i][j] & 0xff000000) >> 24;
                int nrval = applyPoly(poly.get(1),rval);
                int ngval = applyPoly(poly.get(2),gval);
                int nbval = applyPoly(poly.get(3),bval);
                int combined = 0xff000000 | (nrval << 16) | (ngval << 8) | nbval;
//				System.out.println();
//				System.out.println(rval+" "+ gval+" "+bval+" "+" "+nrval+" "+ngval + " " + nbval+ " " + combined );
//				System.exit(0);
                newImage.setPixel(j,i,combined);
            }
        }
        return newImage;
    }

    private static int applyPoly(double[] poly,int x){
        double ans = 0;
        for(int i=0;i<poly.length;i++){
            double k= poly[i]*Math.pow(x, poly.length-i-1);
            ans+=k;
        }
        //ans = Math.ceil(ans);
        long myans = Math.round(ans);
        if(myans>255)
            myans = 255;
        if(myans<0)
            myans = 0;
        return (int)myans;
    }

}
