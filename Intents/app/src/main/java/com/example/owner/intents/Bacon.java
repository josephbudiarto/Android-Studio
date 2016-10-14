package com.example.owner.intents;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class Bacon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle applesData = getIntent().getExtras();
        if(applesData==null){
         return;
        }
        String appleMessage = applesData.getString("appleMessage");
        EditText bacon_input = (EditText)findViewById(R.id.bacon_input);
        bacon_input.setText(appleMessage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void onClick(View view){
        Intent I = new Intent(this, Apples.class);
        startActivity(I);
    }
}
