package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 21.05.15.
 */
public class Animation{
    protected List<Bitmap> spriteSetLeft;
    protected List<Bitmap> spriteSetRight;
    private int iterator;
    protected int animationSize;

    public Animation(ArrayList<Bitmap> spritesLeft, ArrayList<Bitmap> spritesRight){
        spriteSetLeft = spritesLeft;
        spriteSetRight = spritesRight;
        iterator = -1;
    }
    public Bitmap next(List<Bitmap> spriteSet){
        if(iterator >= spriteSet.size()-1){
            iterator = 0;
        }
        else iterator++;
        return spriteSet.get(iterator);
    }

    public Bitmap getNextSprite(int direction){
        if(direction == 1) return next(spriteSetRight);
                else return next(spriteSetLeft);
    }
}
