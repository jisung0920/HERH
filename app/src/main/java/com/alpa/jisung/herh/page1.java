package com.alpa.jisung.herh;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import it.sephiroth.android.library.picasso.MemoryPolicy;
import it.sephiroth.android.library.picasso.NetworkPolicy;
import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by jisung on 2017-08-08.
 */

public class page1 extends android.support.v4.app.Fragment {
    private int imgSrc;
    private Context context;

    public page1(int imgSrc ,Context context) {
        this.imgSrc = imgSrc;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.img_page,container,false);
        ImageView img = (ImageView)linearLayout.findViewById(R.id.img) ;
        Picasso.with(context)
                .load("http://alpahers.cafe24.com/ad_img/"+imgSrc+".png").memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(img);
                return linearLayout;
    }
}