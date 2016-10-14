package com.example.owner.sql;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText input;
    TextView text_view;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        input = (EditText)findViewById(R.id.input);
        text_view = (TextView) findViewById(R.id.text_view);
        dbHandler = new MyDBHandler(this,null,null,1);
        printDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        text_view.setText(dbString);
        input.setText("");
    }

    public void addButtonClick(View view){
        products product = new products(input.getText().toString());
        dbHandler.addProduct(product);
        printDatabase();
    }

    public void deleteButtonClick(View view){
        String inputText = input.getText().toString();
        dbHandler.deleteProduct(inputText);
        printDatabase();
    }

}
