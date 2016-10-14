package com.example.owner.fragments;


import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BottomPictureFragment extends Fragment{

    private static TextView textViewtop;
    private static TextView textViewbottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_picture_fragment, container, false);

        textViewtop = (TextView)view.findViewById(R.id.textViewtop);
        textViewbottom = (TextView)view.findViewById(R.id.textViewbottom);

        return view;
    }

    public void setMemeText(String top, String bottom){
        textViewtop.setText(top);
        textViewbottom.setText(bottom);
    }

}
