package com.example.jisung.herh;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jisung on 2017-07-21.
 */

public class reserver_alAdapter extends BaseAdapter {
    Context context;
    ArrayList<reserver_al> alist;

    public reserver_alAdapter(Context context, ArrayList<reserver_al> alist) {
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
            convertView = inflater.inflate(R.layout.al_list,null);
        TextView date = (TextView)convertView.findViewById(R.id.date);
        TextView time = (TextView)convertView.findViewById(R.id.time);
        TextView num = (TextView)convertView.findViewById(R.id.number);
        TextView state = (TextView)convertView.findViewById(R.id.state);
        date.setText(alist.get(position).getDate());
        time.setText(alist.get(position).getTime().substring(0,5)+"시");
        num.setText(alist.get(position).getNum()+"("+alist.get(position).getError()+")"+"명");
        int i = alist.get(position).getAllow();
        switch (i) {
            case 0:
                state.setText("대기");
                state.setBackgroundResource(R.color.wait);
                break;
            case 1:
                state.setText("승인");
                state.setBackgroundResource(R.color.accept);
                break;
            case 2:
                state.setText("거절");
                state.setBackgroundResource(R.color.refuse);
                break;
            default:
                state.setText("취소");
        }
        return convertView;
    }
}
