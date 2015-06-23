package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by root on 30.05.15.
 */
public class AntRedHunter extends Ant {
    //как только появляется, сразу ищет себе жертву
    public AntRedHunter(int X, int Y, Context context,BattleField field,AntHill hill){
        super(X,Y,context,field,hill);
        hp = 10;
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antr1l));
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antr2l));
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antr3l));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antr1r));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antr2r));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antr3r));
    }
}
