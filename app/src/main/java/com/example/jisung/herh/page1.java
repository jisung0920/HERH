package com.example.jisung.herh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by jisung on 2017-08-08.
 */

public class page1 extends android.support.v4.app.Fragment {
    private int imgSrc;

    public page1(int imgSrc) {
        this.imgSrc = imgSrc;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.img_page,container,false);
        ImageView img = (ImageView)linearLayout.findViewById(R.id.img) ;
        img.setImageResource(imgSrc);
                return linearLayout;
    }
}