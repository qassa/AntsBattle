package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by root on 21.05.15.
 */
public class AntWarrior extends Ant {
    private Ant currentEnemy;
    private int eyesight = 500;
    private AntWarrior me;

    public AntWarrior(){
        throw new RuntimeException("Illegal data for Warrior ant");
    }

    public AntWarrior(int X, int Y, Context context,BattleField field,AntHill hill){
        super(X,Y,context,field,hill);
        hp = 7;
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.anta1l));
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.anta2l));
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.anta3l));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.anta1r));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.anta2r));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.anta3r));
        me = this;
        someMoving = new Observe();
        someMoving.start();
    }

    @Override
    public void startMovement(int x,int y) throws InterruptedException {
        if(someMoving!=null) someMoving.interrupt();
        destinationX = x;
        destinationY = y;
        someMoving = new Movement();
        someMoving.start();
        try {
            someMoving.join();
        } catch (InterruptedException e) {
            someMoving.interrupt();
        }
    }

    public void Attact(Ant enemy){

    }

    public void Fire(Ant enemy){

    }

    public class goToVictim extends Thread{
        @Override
        public void run(){

        }
    }

    public class Observe extends Thread{
        @Override
        public void run(){
            while(!isInterrupted()){
                try {
                currentEnemy = field.scanTerritory(me,500);
                if(currentEnemy!= null)
                    interrupt();
                sleep(1000);
                } catch (InterruptedException e) {
                    currentThread().interrupt();
                }
            }
        }
    }
}
