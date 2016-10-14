package com.example.owner.listview;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
class CustomAdapter extends ArrayAdapter<String>{
    public CustomAdapter(Context context, String[] foods) {
        super(context, R.layout.custom_row,foods);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater buckysInflater = LayoutInflater.from(getContext());
        View customView = buckysInflater.inflate(R.layout.custom_row,parent, false);

        String singleFoodItem = getItem(position);
        TextView text_view = (TextView) customView.findViewById(R.id.text_view);
        ImageView image_view = (ImageView)customView.findViewById(R.id.image_view);

        text_view.setText(singleFoodItem);
        image_view.setImageResource(R.drawable.airasia);
        return customView;
    }
}