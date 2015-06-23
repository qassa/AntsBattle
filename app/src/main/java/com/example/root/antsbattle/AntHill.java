package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Created by root on 30.05.15.
 */
public class AntHill extends GameObject{
    //public AntWorker spawnAntWorker(){
    //    return new AntWorker();
    //}
    public int accomodates = 7;
    public int armyCost = 3;
    public int workCost = 2;
    public BattleField field;
    public int XPosition;
    public int YPosition;
    private int currentStock;
    private int level;
    private Context context;
    private int spawnAntPosX;
    private int spawnAntPosY;

    Random rand = new Random();
    public AntHill(int X, int Y, Context context,BattleField field){
        super(X,Y, BitmapFactory.decodeResource(context.getResources(), R.drawable.smallhill));
        XPosition = X; YPosition = Y;
        this.field = field;
        currentStock = 0;
        level = 1;
        this.context = context;
    }

    public void createMenu(){

    }

    public void closeMenu(){

    }

    public void randomSpawnPosition(){
        int side = rand.nextInt(4);
        switch (side){
            case 0: {spawnAntPosX = rand.nextInt(this.bottomX+3-this.topX)+this.topX-1; spawnAntPosY = this.topY - 34;}
            case 1: {spawnAntPosX = this.bottomX + 34; spawnAntPosY = rand.nextInt(this.bottomY+3-this.topY)+this.topY-1;}
            case 2: {spawnAntPosX = rand.nextInt(this.bottomX+3-this.topX)+this.topX-1; spawnAntPosY = this.bottomY + 34;}
            case 3: {spawnAntPosX = this.topX - 34; spawnAntPosY = rand.nextInt(this.bottomY+3-this.topY)+this.topY-1;}
        }
    }

    public void harwest(){
        AntHillGrows();
        currentStock++;
    }

    public int scope() {return currentStock;}

    public void createWorker(){
        randomSpawnPosition();
        AntWorker ant = new AntWorker(spawnAntPosX,spawnAntPosY,context,field,this);
        field.ants.add(ant);
    }

    public void createWarrior(){
        randomSpawnPosition();
        AntWarrior ant = new AntWarrior(spawnAntPosX,spawnAntPosY,context,field,this);
        field.ants.add(ant);
    }

    public void AntHillGrows(){
        if(level==1)
            //if(field.amountOfAnts(this)>=3)
            if(currentStock > 5)
                this.currentSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.mediumrhill);

        //if(level==2)
        if(currentStock > 7)
            if(field.amountOfAnts(this)>=5)
                this.currentSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.largehill);

        //if(level==3)
        if(currentStock > 9)
            if(field.amountOfAnts(this)>=7)
                this.currentSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.hugehill);
    }

    public void antsGoAside(){

    }
}
