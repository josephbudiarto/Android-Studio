package com.example.owner.cobaimageview;

import android.app.Activity;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] foods = {"Bacon","Ham","Tuna","Candy","Meatball","Potato"};

        ListAdapter buckysAdapter = new CustomAdapter(this,foods);
        ListView buckysListView = (ListView)findViewById(R.id.buckyslistview);
        buckysListView.setAdapter(buckysAdapter);
    }
}