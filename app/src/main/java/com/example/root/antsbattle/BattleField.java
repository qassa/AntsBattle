package com.example.root.antsbattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by root on 21.05.15.
 */
public class BattleField{
    public List<Ant> ants = Collections.synchronizedList(new ArrayList<Ant>());
    public List<AntHill> antHills = new ArrayList<>();
    public List<Food> harvest = Collections.synchronizedList(new ArrayList<Food>());
    //public List<innerRectangle> busyTerritory = new ArrayList<innerRectangle>(); gameobjects
    public Ant selectedAnt = null;
    private Context context;
    private GameObject selectedGameObject;
    private boolean longSwipe;
    public Food nowCarried;
    public Bitmap map;
    private GameEvents eventThread;
    private int mapHeight;
    private int mapWidth;
    private List<Bitmap> foodVariants;
    private AntHill selectedHill;

    public BattleField(Context context){
        this.context = context;
        map = BitmapFactory.decodeResource(context.getResources(), R.drawable.stagnogley);
        mapHeight = map.getHeight();
        mapWidth = map.getWidth();

        foodVariants = new ArrayList<>();

        foodVariants.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.raspberry));
        foodVariants.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.leaf));
        foodVariants.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.acorn));
        foodVariants.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.berry));
    }

    public void createWorld(){
        eventThread = new GameEvents();
        eventThread.start();
        spawnAntHill();
    }

    public synchronized void removeAnt(Ant ant){
        Iterator<Ant> it = ants.iterator();
        while(it.hasNext()){
            Ant checkingAnt = it.next();
            if(checkingAnt.hashCode() == (ant.hashCode())){
                if(checkingAnt.equals(selectedAnt))
                    selectedAnt = null;
                it.remove();
            }
        }
    }

    public synchronized void removeFood(Food food){
        Iterator<Food> it = harvest.iterator();
        while(it.hasNext()){
            Food checkingFood = it.next();
            if(checkingFood.equals(food)){
                it.remove();
            }
        }
    }

    public Boolean crossing(int topX1, int topY1, int bottomX1, int bottomY1, int topX2, int topY2, int bottomX2, int bottomY2){
        return ((topX1 >= topX2 && topX1 <= bottomX2) && (topY1 >= topY2 && topY1 <= bottomY2));
                //||  ((bottomX1 >= topX2 && bottomX1 <= bottomX2) && (bottomY2 >= topY2 && bottomY2 <= bottomY2));
    }

    public void killAntsAround(Food food) {
        synchronized (ants) {
            Iterator<Ant> it = ants.iterator();
            while (it.hasNext()) {
                Ant ant = it.next();
                if (crossing(ant.topX, ant.topY, ant.bottomX, ant.bottomY, food.topX, food.topY, food.bottomX, food.bottomY)) {
                    ant.Die();
                    if(ant instanceof AntWorker && ((AntWorker) ant).carries != null)
                        ((AntWorker) ant).carries.canBeTouched = true;
                    it.remove();
                    if(ant.equals(selectedAnt)) selectedAnt = null;
                }
            }
        }
    }

    public int amountOfAnts(AntHill hill){
        synchronized (ants) {
            int amount = 0;
            Iterator<Ant> it = ants.iterator();
            while (it.hasNext()) {
                Ant iteratingAnt = it.next();
                    if(iteratingAnt.hill.equals(hill)) amount++;
            }
            return amount;
        }
    }

    public Food whatFoodSelected(int x, int y){
        synchronized (harvest) {
            Iterator<Food> it = harvest.iterator();
            while(it.hasNext()){
                Food iteratingFood = it.next();
                if (iteratingFood.isObjectTouched(x, y))
                    return iteratingFood;
            }
        }
        return null;
    }

    public GameObject whatSelected(int x, int y){
        synchronized (antHills) {
            Iterator<AntHill> it = antHills.iterator();
            while(it.hasNext()){
                AntHill iteratingHill = it.next();
                if (iteratingHill.isObjectTouched(x,y))
                    return iteratingHill;
            }
        }
        synchronized (ants) {
            Iterator<Ant> it = ants.iterator();
            while (it.hasNext()) {
                Ant iteratingAnt = it.next();
                if (iteratingAnt.isObjectTouched(x, y))
                    return iteratingAnt;
            }
        }
        if(whatFoodSelected(x,y)!=null) return whatFoodSelected(x,y);
        return null;
    }

    public void antRules(Ant ant, int x,int y){
        if(selectedHill != null) {selectedHill = null;}
        if(selectedAnt != null && selectedAnt.equals(ant))
        {
            Food f = whatFoodSelected(x,y);
            if(f!=null) foodRules(f);
        }
        if(selectedAnt!=null)
            selectedAnt = ant;
        else
            moveAnt(x, y);
        selectedAnt = ant;
    }

    public void foodRules(Food food){
        if(selectedHill != null) {selectedHill = null;}
        if(selectedAnt==null) return;
            else if(food.canBeTouched == true && selectedAnt instanceof AntWorker && ((AntWorker) selectedAnt).carries == null)
            ((AntWorker) selectedAnt).Harvest(food);
    }

    public void hillRules(AntHill hill){
        selectedHill = hill;
        if(selectedAnt!=null && selectedAnt instanceof AntWorker && ((AntWorker) selectedAnt).carries != null)
            ((AntWorker) selectedAnt).startMovementWithFood(selectedAnt.hill.XPosition,selectedAnt.hill.topY);
    }

    public void moveAnt(int x, int y){
        if(selectedAnt != null && !longSwipe)

            try {
                selectedAnt.startMovement(x, y);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public void commonRules(int x, int y){
        moveAnt(x,y);
    }

    public void checkRules(float X, float Y, boolean longSwipe) {
        int x = Math.round(X);
        int y = Math.round(Y);
        this.longSwipe = longSwipe;
        selectedGameObject = whatSelected(x,y);

        if(selectedGameObject instanceof AntHill) hillRules((AntHill) selectedGameObject); else
        if(selectedGameObject instanceof Ant) antRules((AntWorker) selectedGameObject, x, y); else
        if(selectedGameObject instanceof Food) foodRules((Food) selectedGameObject);
        else commonRules(x, y);
    }

    public void spawnAntHill(){
        int posX = new Random().nextInt(500)+250; //mapWidth-500
        int posY = new Random().nextInt(500)+250;
        AntHill hill = new AntHill(posX,posY,context,this);
        hill.createWorker();
        hill.createWorker();
        hill.createWorker();
        hill.createWorker();
        hill.createWorker();
        antHills.add(hill);
    }

    public synchronized void spawnFood(){
        Food food = new Food(new Random().nextInt(500 - 200) + 100, new Random().nextInt(500 - 200) + 100, foodVariants.get(new Random().nextInt(4)),this);
        Thread dowing = food.down();
        harvest.add(food);
        try {
            dowing.join();
            food.canBeTouched = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void spawnRedAnt(){

    }

    public Ant scanTerritory(AntWarrior war,int eye){
        List<Ant> foundAnts = new ArrayList<>();
        synchronized (ants) {
            Iterator<Ant> it = ants.iterator();
            while (it.hasNext()) {
                Ant ant = it.next();
                if (ant.getX()>=war.getX()-250 && ant.getX()<=war.getX()+250 && ant.getY()>=war.getY()-250 && ant.getY()<=war.getY()+250)
                    foundAnts.add(ant);
            }
        }
        Ant closeAnt = null;
        for(int i=0; i<foundAnts.size()-1;i++){
            if (GameObject.road(war,foundAnts.get(i)) > GameObject.road(war,foundAnts.get(i + 1))){
                closeAnt = foundAnts.get(i+1);
            }
        }
        return closeAnt;
    }

    public class GameEvents extends Thread{

        @Override
        public void run(){
            Random rand = new Random();
            while (!Thread.interrupted()){
                try {
                    int harvestChance = rand.nextInt(15)+1;
                    if(harvestChance == 1)
                        spawnFood();
                    int redHunterChance = rand.nextInt(99)+1;
                    if(redHunterChance==1)
                        spawnRedAnt();
                    sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
