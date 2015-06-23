package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Iterator;

/**
 * Created by root on 18.05.15.
 */
public class DoMagic extends SurfaceView implements SurfaceHolder.Callback {
    private BattleField field;
    private MagicThread magicThread;

    private static final int INVALID_POINTER_ID = -1;
    private int mPointerIndex = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;

    private float _xoffset = 0;
    private float _yoffset = 0;
    private float _lastTouchX = 0;
    private float _lastTouchY = 0;

    private float startTouchX = 0;
    private float startTouchY = 0;
    private float boundaryX;
    private float boundaryY;
    private float canvasX;
    private float canvasY;
    private boolean longSwipe = false;

    @Override
    public void surfaceCreated (SurfaceHolder holder){
        magicThread = new MagicThread(getHolder());
        magicThread.setRunning(true);
        magicThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        magicThread.setRunning(false);
        while(retry){
            try{
                magicThread.join();
                retry = false;
            }
            catch (InterruptedException ex){
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        mScaleDetector.onTouchEvent(ev);
        final int action = ev.getAction();
        switch(action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
                final float x = ev.getX();
                final float y = ev.getY();
                startTouchX = x;
                startTouchY = y;

                _lastTouchX = x;
                _lastTouchY = y;
                mPointerIndex = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                final int  pointerIndex = ev.findPointerIndex(mPointerIndex);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                if(!mScaleDetector.isInProgress()){
                    final float dx = x - _lastTouchX;
                    final float dy = y - _lastTouchY;

                    if(dx+_xoffset >= 0 ) _xoffset = 0; else if(dx+_xoffset <= boundaryX+canvasX) _xoffset = boundaryX+canvasX; else _xoffset += dx;
                    if(dy+_yoffset >= 0 ) _yoffset = 0; else if(dy+_yoffset <= boundaryY+canvasY) _yoffset = boundaryY+canvasY; else _yoffset += dy;
                }
                _lastTouchX = x;
                _lastTouchY = y;
                break;
            }
            case MotionEvent.ACTION_UP:{
                mPointerIndex = INVALID_POINTER_ID;
            }
            case MotionEvent.ACTION_CANCEL:{
                mPointerIndex = INVALID_POINTER_ID;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)>>MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                longSwipe = longSwipe(startTouchX, startTouchY, _lastTouchX, _lastTouchY);
                field.checkRules(Math.abs(startTouchX+Math.abs(_xoffset)),Math.abs(startTouchY+Math.abs(_yoffset)),longSwipe);
                if (pointerId == mPointerIndex){
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    _lastTouchX = ev.getX(newPointerIndex);
                    _lastTouchY = ev.getY(newPointerIndex);
                    mPointerIndex = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return  true;
    }

    public boolean longSwipe(float x1, float y1, float x2, float y2){
        if((Math.max(x2,x1)-Math.min(x2,x1)>=5) || (Math.max(y2,y1)-Math.min(y2,y1)>=5)) return true;
        else return false;
    }

    public DoMagic(Context context, BattleField field){
        super(context);
        boundaryX = -field.map.getWidth();
        boundaryY = -field.map.getHeight();
        this.field = field;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener());
        getHolder().addCallback(this);
    }

    public class MagicThread extends Thread{
        private boolean running = false;
        private SurfaceHolder surfaceHolder;
        //private boolean suspendFlag = false;

        public MagicThread(SurfaceHolder surfaceHolder){
            this.surfaceHolder = surfaceHolder;
        }
        public void setRunning(boolean running){
            this.running = running;
        }
        @Override
        public void run(){
            Canvas canvas;
            while(running){
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    canvas.drawColor(Color.BLACK);
                    canvasX = canvas.getWidth();
                    canvasY = canvas.getHeight();
                    canvas.save();
                    canvas.translate(_xoffset, _yoffset);
                    canvas.drawBitmap(field.map, 0, 0, null);

                    Paint p = new Paint();
                    p.setColor(Color.YELLOW);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(5);
                    p.setTextSize(30);

                    //Отрисовка муравейника
                    synchronized(field.antHills) {
                        Iterator<AntHill> it = field.antHills.iterator();
                        while(it.hasNext()){
                            AntHill toDisplay = it.next();
                            canvas.drawBitmap(toDisplay.currentSprite, toDisplay.topX, toDisplay.topY, null);
                            //canvas.drawRect(toDisplay.topX, toDisplay.topY, toDisplay.bottomX, toDisplay.bottomY, p);
                        }
                    }

                    //Отрисовка урожая
                    synchronized(field.harvest) {
                        Iterator<Food> it = field.harvest.iterator();
                        while(it.hasNext()){
                            Food toDisplay = it.next();
                            canvas.drawBitmap(toDisplay.currentSprite, toDisplay.topX, toDisplay.topY, null);
                            //canvas.drawRect(toDisplay.topX, toDisplay.topY, toDisplay.bottomX, toDisplay.bottomY, p);
                        }
                    }

                    //Отрисовка муравьев, сначала своих - потом вражеских
                    synchronized(field.ants) {
                        Iterator<Ant> it = field.ants.iterator();
                        while(it.hasNext()){
                            Ant toDisplay = it.next();
                            canvas.drawBitmap(toDisplay.currentSprite, toDisplay.topX, toDisplay.topY, null);
                            //canvas.drawRect(toDisplay.topX, toDisplay.topY, toDisplay.bottomX, toDisplay.bottomY, p);
                        }
                    }

                    //Отрисовка летящих снарядов

                    //Отрисовка поднятого муравьем ореха
                    //if(field.nowCarried != null)


                    //Рамка выделенного юнита
                    if(field.selectedAnt != null)
                        canvas.drawRect(field.selectedAnt.topX, field.selectedAnt.topY,field.selectedAnt.bottomX,field.selectedAnt.bottomY,p);

                    //самая последняя - отрисовка активного окна муравейника


                    canvas.restore();
                    canvas.drawText(Float.toString(field.map.getHeight()), 100, 100, p);
                    canvas.drawText(Float.toString(field.map.getWidth()), 100, 200, p);
                    canvas.drawText(Integer.toString(field.antHills.get(0).scope()), 100, 300, p);
                }
                    finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
            }
        }
    }
}
