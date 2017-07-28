package com.example.jisung.herh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jisung on 2017-06-02.
 */

public class storeAdapter extends BaseAdapter {
    Context context;
    ArrayList<Store> data;

    public storeAdapter(Context context, ArrayList<Store> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.store_list_item, null);

        ImageView img = (ImageView) convertView.findViewById(R.id.storeimg);
        img.setImageResource(data.get(position).getImg());


        return convertView;
    }
}
