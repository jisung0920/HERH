package com.example.jisung.herh;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jisung on 2017-07-20.
 */

public class reserverAdapter  extends BaseAdapter {
    Context context;
    ArrayList<reserver> alist;

    public reserverAdapter(Context context, ArrayList<reserver> alist) {
        this.context = context;
        this.alist = alist;
    }
    @Override
    public int getCount() {
        return alist.size();
    }

    @Override
    public Object getItem(int position) {
        return alist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(convertView == null)
            convertView = inflater.inflate(R.layout.r_list_item,null);

        TextView txtview = (TextView)convertView.findViewById(R.id.name);
        TextView res_tel = (TextView)convertView.findViewById(R.id.res_tel);
        txtview.setText(alist.get(position).getName());
        res_tel.setText(alist.get(position).getTime());
        return convertView;
    }
}
