package com.example.root.antsbattle;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class InitializeActivity extends Activity {
    public DoMagic graphics;
    public BattleField field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        field = new BattleField(this);
        graphics = new DoMagic(this,field);
        setContentView(graphics);

        field.createWorld();
    }

}
