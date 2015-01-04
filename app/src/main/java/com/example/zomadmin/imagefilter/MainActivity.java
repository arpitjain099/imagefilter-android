package com.example.zomadmin.imagefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    ApplyCurve curve1;
    double[][] GaussianBlurConfig = new double[][] {
            { 1, 2, 1 },
            { 2, 4, 2 },
            { 1, 2, 1 }
    };
    double filter[][] = new double[][] {
        {1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap normal_sample = BitmapFactory.decodeResource(getResources(),R.drawable.newsample);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        final Bitmap newBitmap = Bitmap.createScaledBitmap(normal_sample,width-20,height - 470,true);
        final MyView myview = (MyView) findViewById(R.id.view1);
        myview.changeBitmap(newBitmap,false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myview.invalidate();
            }
        });

        //for button 1
        ImageView im1 = (ImageView) findViewById(R.id.imageView1);
        im1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                myview.changeBitmap(newBitmap,false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

        //intialize the curves
        try {
            curve1 = new ApplyCurve("country.acv", getApplicationContext());
        }catch (IOException e){
            Log.d("EX", "Unable to gen poly for curve");
        }

        ImageView im2 = (ImageView) findViewById(R.id.imageView2);
        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curve1!=null){
                    Bitmap nb = curve1.getModifiedImage(newBitmap);
                    myview.changeBitmap(nb,false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myview.invalidate();
                        }
                    });
                }
            }
        });

        //gaussian blur
        final Convolution convMatrix = new Convolution(3);
        convMatrix.applyConfig(GaussianBlurConfig);
        convMatrix.Factor = 16;
        convMatrix.Offset = 0;
        ImageView im3 = (ImageView) findViewById(R.id.imageView3);
        im3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bitmap nb = Convolution.computeConvolution3x3(newBitmap,convMatrix);
                myview.changeBitmap(nb,false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

        //Motion  Blur
        final Convolution convolution = new Convolution(9);
        convolution.applyConfig(filter);
        convolution.Factor = 1.0;
        convolution.Offset = 9.0;
        ImageView im4 = (ImageView) findViewById(R.id.imageView4);
        im4.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bitmap nb = Convolution.computeConvolution3x3(newBitmap,convolution);
                myview.changeBitmap(nb,false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

        //GrayScale
        ImageView im5 = (ImageView) findViewById(R.id.imageView5);
        im5.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bitmap nb = BitmapClassics.doGreyscale(newBitmap);
                myview.changeBitmap(nb,false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

        //Gamma Correction 1.8 1.8 1.8
        ImageView im6 = (ImageView) findViewById(R.id.imageView6);
        im6.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bitmap nb = BitmapClassics.doGamma(newBitmap,1.8,1.8,1.8);
                myview.changeBitmap(nb,false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

        //Filter color
        ImageView im7 = (ImageView) findViewById(R.id.imageView7);
        im7.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bitmap nb = BitmapClassics.doColorFilter(newBitmap,1,0,0);
                myview.changeBitmap(nb,false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

        //Tint
        ImageView im8 = (ImageView) findViewById(R.id.imageView8);
        im8.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bitmap nb = BitmapClassics.tintImage(newBitmap,50);
                myview.changeBitmap(nb,false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

        //Vignette
        ImageView im9 = (ImageView) findViewById(R.id.imageView9);
        im9.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                myview.changeBitmap(newBitmap,true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myview.invalidate();
                    }
                });
            }
        });

    }

//    public void writeOutputToFile(Bitmap bmp){
//        try
//        {
//            MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "f.png", "");
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

}
