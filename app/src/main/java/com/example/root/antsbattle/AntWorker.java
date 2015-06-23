package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by root on 30.05.15.
 */
public class AntWorker extends Ant {
    public Food carries;
    private boolean inHill = false;

    public AntWorker(int X, int Y, Context context,BattleField field,AntHill hill){
        super(X, Y, context, field, hill);
        hp = 3;
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antw1l));
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antw2l));
        movingLeft.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antw3l));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antw1r));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antw2r));
        movingRight.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.antw3r));
    }

    public void startMovementWithFood(int x, int y){
        if(field.whatSelected(x,y) instanceof AntHill) inHill = true;
        if(someMoving!=null) someMoving.interrupt();
        destinationX = x;
        destinationY = y;
        someMoving = new MovementWithFood();
        someMoving.start();
    }

    public void Harvest(Food food){
        if(food.canBeTouched == true){
            carries = food;
            if(someMoving!=null) someMoving.interrupt();
            someMoving = new DoHarwest();
            someMoving.start();
        }
    }


    public class DoHarwest extends Thread{
        @Override
        public void run(){
            Thread curr=null;
            while(!isInterrupted()) {
                try {
                    carries.startInfiniteLife();
                    destinationX = carries.getXPosition();
                    destinationY = carries.getYPosition();
                    curr = new Movement();
                    curr.start();
                    curr.join();
                    destinationX = hill.XPosition;
                    destinationY = hill.topY;
                    inHill = true;
                    curr = new MovementWithFood();
                    curr.start();
                    carries.canBeTouched = false;
                    curr.join();
                    field.removeFood(carries);
                    carries = null;
                    inHill = false;
                    hill.harwest();
                    hill.AntHillGrows();
                    Thread.currentThread().interrupt();
                } catch (InterruptedException e) {
                    carries.breakInfiniteLife();
                    inHill = false;
                    carries.canBeTouched = true;
                    carries = null;
                    curr.interrupt();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public class MovementWithFood extends Thread{
        public MovementWithFood(){
        }
        double b = Math.max(destinationX,x)-Math.min(destinationX,x);
        double c = Math.max(destinationY,y)-Math.min(destinationY,y);
        float a = (float)Math.sqrt(Math.pow(b,2)+Math.pow(c,2));
        float speed = 0.1f;
        int animationTick = 1;
        Animation anim = new SimpleMoving(movingLeft,movingRight);
        int startX = x;
        int startY = y;

        @Override
        public void run() {
            a1 = a;
            long start = System.currentTimeMillis();
            rotation = getMovingDirection(destinationX-startX,destinationY-startY);

            while ((!Thread.interrupted()) && (x!=destinationX)) {
                try {
                    animationTick++; if(animationTick % 20 == 0) currentSprite = anim.getNextSprite(rotation);
                    long elapsedTime = System.currentTimeMillis() - start;
                    setX(lerp(startX, destinationX, a / speed, elapsedTime));
                    setY(lerp(startY,destinationY,a/speed,elapsedTime));
                    if(animationTick>=99) animationTick = 0;
                    if(carries!=null){carries.setXPosition(x); carries.setYPosition(topY-30);}
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            //if(carries!=null)
            //if(inHill==true)
            //    {
             //       field.removeFood(carries);
             //       carries = null;
             //       inHill = false;
             //   }
        }
    }

}
