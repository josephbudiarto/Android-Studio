package com.example.owner.amuzemuseum;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

class CustomAdapter extends ArrayAdapter<String>{
    public CustomAdapter(Context context, ArrayList<String> foods) {
        super(context, R.layout.row_layout,foods);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater buckysInflater = LayoutInflater.from(getContext());
        View customView = buckysInflater.inflate(R.layout.row_layout,parent, false);

        String singleFoodItem = getItem(position);
        TextView text_view = (TextView) customView.findViewById(R.id.tx_name);
        //ImageView image_view = (ImageView)customView.findViewById(R.id.image_view);

        text_view.setText(singleFoodItem);
        //image_view.setImageResource(R.drawable.airasia);
        return customView;
    }
}