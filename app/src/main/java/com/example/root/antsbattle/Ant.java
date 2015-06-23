package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by root on 20.05.15.
 */
public class Ant extends GameObject{
    protected int x = 0;
    protected int y = 0;
    protected int destinationX;
    protected int destinationY;
    public Fraction fraction;
    public int rotation;
    //муравей может одноывременно выполнять только одно действие
    //передвигаться, пускать железу и т.д. этот поток отвечает за это
    protected Thread someMoving;
    public float a1;
    private Context resourceContext;
    public int hp;
    protected BattleField field;
    public AntHill hill;
    protected ArrayList<Bitmap> movingLeft = new ArrayList<>();
    protected ArrayList<Bitmap> movingRight = new ArrayList<>();

    public Ant(){}

    public Ant(int X, int Y, Context context, BattleField field, AntHill hill){
        super(X, Y, BitmapFactory.decodeResource(context.getResources(), R.drawable.antw1l));
        setX(X);
        setY(Y);
        resourceContext = context;
        this.field = field;
        this.hill = hill;
    }

    public void setX(int X){
        x = X;
        topX = x - width/2;
        bottomX = x + width/2;
    }
    public void setY(int Y){
        y = Y;
        topY = y - height/2;
        bottomY = y + height/2;
    }
    public int getX(){
        return x;
    }

    public int getY(){return y;}

    public void startMovement(int x, int y) throws InterruptedException {
        if(someMoving!=null) someMoving.interrupt();
        destinationX = x;
        destinationY = y;
        someMoving = new Movement();
        someMoving.start();
    }

    public void Die(){
        if(someMoving!=null) someMoving.interrupt();
    }

    public int getMovingDirection(int x, int y){
        double a = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        //double sin = y/a;
        double cos = x/a;

        //if(cos>0.9239 && cos<=1 && sin>-0.3827 && sin<=0.3827) return 0;
        //if(cos>0.3827 && cos<=0.9239 && sin>-0.9239 && sin<=-0.3827) return 1;
        //if(cos>-0.3827 && cos<=0.3827 && sin>-1 && sin<=-0.9239) return 2;
        //if(cos>-0.9239 && cos<=-0.3827 && sin>-0.9239 && sin<=-0.3827) return 3;
        //if(cos>-1 && cos<=-0.9239 && sin>-0.3827 && sin<=0.3827) return 4;
        //if(cos>-0.9239 && cos<=-0.3827 && sin>0.3827 && sin<=0.9239) return 5;
        //if(cos>-0.3827 && cos<=0.3827 && sin>0.9239 && sin<=1) return 6;
        //if(cos>0.3827 && cos<=0.9239 && sin>0.3827 && sin<=0.9239) return 7;
        if(cos>=0) return 1;
        else
        return 0;
    }

    public int lerp(float start, float target, float duration, float timeSinceStart){
        float value = start;
        if(timeSinceStart>=0.0f && timeSinceStart < duration){
            float range = target - start;
            float percent =timeSinceStart / duration;
            value = start + range * percent;
        }
        else if(timeSinceStart>=duration)
            value = target;
        return Math.round(value);
    }

    public class Movement extends Thread{
        public Movement(){
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
                    setY(lerp(startY, destinationY, a / speed, elapsedTime));
                    if(animationTick>=99) animationTick = 0;
                    //setY(Math.round(((x - startX)*(destinationY-startY))/(destinationX-startX))-startY);
                    Thread.sleep(10);
                    //throw  new RuntimeException();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

        }
    }

}
