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
        TextView time = (TextView)convertView.findViewById(R.id.time);
        TextView name = (TextView)convertView.findViewById(R.id.name);
        TextView num = (TextView)convertView.findViewById(R.id.number);
        time.setText(alist.get(position).getTime().substring(0,5));
        name.setText(alist.get(position).getName());
        num.setText(alist.get(position).getNum()+"("+alist.get(position).getError()+")"+"명");
        return convertView;
    }
}
