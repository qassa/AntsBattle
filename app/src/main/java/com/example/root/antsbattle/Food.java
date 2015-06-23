package com.example.root.antsbattle;

import android.graphics.Bitmap;

/**
 * Created by root on 30.05.15.
 */
public class Food extends GameObject {
    //Орехи, литья и прочие вкусности не могут падать на муравейники (убьют муравья, если упадут на него)
    private int XPosition;
    private int YPosition;
    private int curY = -50;
    private int dest;
    private Food thisObj;
    private BattleField field;
    public boolean canBeTouched = false;
    public Thread currentLive;

    public Food(int X, int Y, Bitmap skin,BattleField field){
        super(X,Y,skin);
        XPosition = X; YPosition = Y;
        thisObj = this;
        this.field = field;
    }

    public int getXPosition(){
        return XPosition;
    }

    public void setXPosition(int x){
        XPosition = x;
        topX = x - width/2;
        bottomX = x + width/2;
    }

    public int getYPosition(){
        return YPosition;
    }

    public void setYPosition(int y){
        XPosition = y;
        topY = y - height/2;
        bottomY = y + height/2;
    }

    public void setCurrY(int Y){
        curY = Y;
        topY = curY - width/2;
        bottomY = curY + width/2;
    }

    public void breakInfiniteLife(){
        if(currentLive instanceof foodLive)
            currentLive.interrupt();
        currentLive = new foodLive();
        currentLive.start();
    }

    public void startInfiniteLife(){
        if(currentLive != null){
            currentLive.interrupt();
        }
    }

    public DownAction down(){
        DownAction fadingDown = new DownAction();
        fadingDown.start();
        return fadingDown;
    }

    public boolean goDown(int speed){
        setCurrY(curY+speed);
        return curY >= YPosition;
    }

    public boolean jump(){
        setCurrY(curY - 2);
        return curY <= dest;
    }

    public class DownAction extends Thread{
        @Override
        public void run(){
            try {
                while(!isInterrupted()){
                    while(!goDown(7)) sleep(5);
                    field.killAntsAround(thisObj);
                    dest = YPosition - 70;
                    while(!jump()) sleep(30);
                    while(!goDown(2)) sleep(30);
                    currentLive = new foodLive();
                    currentLive.start();
                    Thread.currentThread().interrupt();
                }
                } catch (InterruptedException e) {

                }
        }
    }

    public class foodLive extends Thread{
        @Override
        public void run(){
            try {
                while(!isInterrupted()){
                    sleep(15000); int i = 0;
                    while(i < 5){
                        display = false;
                        sleep(500);
                        display = true;
                        sleep(500); i++;
                    }
                    field.removeFood(thisObj);
                    Thread.currentThread().interrupt();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
