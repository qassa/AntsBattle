package com.example.root.antsbattle;

import android.graphics.Bitmap;

/**
 * Created by root on 23.05.15.
 */
public class GameObject {
    Bitmap currentSprite;
    public int topX;
    public int topY;
    public int bottomX;
    public int bottomY;
    protected int width;
    protected int height;
    public boolean display;
    private int X;
    private int Y;

    public GameObject(){
        throw new RuntimeException("Can't create object without coordinates");
    }
    public GameObject(int X, int Y, Bitmap currentSprite){
        this.currentSprite = currentSprite;
        width = currentSprite.getWidth();
        height = currentSprite.getHeight();
        topX = X-width/2;
        topY = Y-height/2;
        bottomX = X+width/2;
        bottomY = Y+height/2;
        display = true;
        this.Y = Y;
        this.X = X;
    }
    public boolean isObjectTouched(int x, int y){
        if ((x >= this.topX) && (x <= this.bottomX) && (y >= this.topY) && (y <= this.bottomY)) return true;
        else return false;
    }

    public static float road(GameObject A, GameObject B){
        double b = Math.max(A.X, B.X)-Math.min(A.X,B.X);
        double c = Math.max(A.Y,B.Y)-Math.min(A.Y,B.Y);
        return  (float)Math.sqrt(Math.pow(b,2)+Math.pow(c,2));
    }
}
