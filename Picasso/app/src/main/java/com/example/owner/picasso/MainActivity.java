package com.example.owner.picasso;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView image = (ImageView) findViewById(R.id.iv);
        Picasso.with(this).load("http://opensource.petra.ac.id/~rina/image/Museum/MuseumSatriaMandala.jpg").placeholder(R.drawable.airasia).error(R.drawable.lionair).into(image);
    }
}