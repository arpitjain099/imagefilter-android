package com.example.zomadmin.imagefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

//custom view class
public class MyView extends View {

    private Bitmap bitmap;
    private boolean isVignette;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
        if(isVignette){
            Log.d("IMAGE", "Creating a vignette");
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int tenthLeftRight = (int)(width/5);
            int tenthTopBottom = (int)(height/5);
            Shader linGradLR = new LinearGradient(0, height/2, tenthLeftRight/2, height/2, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            // Gradient top - bottom
            Shader linGradTB = new LinearGradient(width/2, 0, width/2, tenthTopBottom, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            // Gradient right - left
            Shader linGradRL = new LinearGradient(width, height/2, (width-tenthLeftRight), height/2, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            // Gradient bottom - top
            Shader linGradBT = new LinearGradient(width/2, height, width/2, (height - tenthTopBottom), Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(linGradLR);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setAlpha(125);
            // Rect for Grad left - right
            Rect rect = new Rect(0, 0, tenthLeftRight, height);
            RectF rectf = new RectF(rect);
            canvas.drawRect(rectf, paint);

            // Rect for Grad top - bottom
            paint.setShader(linGradTB);
            rect = new Rect(0, 0, width, tenthTopBottom);
            rectf = new RectF(rect);
            canvas.drawRect(rectf, paint);

            // Rect for Grad right - left
            paint.setShader(linGradRL);
            rect = new Rect(width, 0, width - tenthLeftRight, height);
            rectf = new RectF(rect);
            canvas.drawRect(rectf, paint);

            // Rect for Grad bottom - top
            paint.setShader(linGradBT);
            rect = new Rect(0, height - tenthTopBottom, width, height);
            rectf = new RectF(rect);
            canvas.drawRect(rectf, paint);
        }
    }

    public void changeBitmap(Bitmap bitmap,boolean isVignette){
        this.bitmap = bitmap;
        this.isVignette = isVignette;
    }


}