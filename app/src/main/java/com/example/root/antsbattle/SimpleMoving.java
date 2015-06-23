package com.example.root.antsbattle;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by root on 21.05.15.
 */
public class SimpleMoving extends Animation {
    public SimpleMoving(ArrayList<Bitmap> l,ArrayList<Bitmap> r){
        super(l,r);
        animationSize = spriteSetLeft.size()-1;
    }
}
