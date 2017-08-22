package com.example.jisung.herh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by jisung on 2017-06-02.
 */

public class storeAdapter extends BaseAdapter {
    Context context;
    ArrayList<Store> data;
    Bitmap bitmap;
    private List<WeakReference<View>> mRecycleList = new ArrayList<WeakReference<View>>();

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

//        bitmap= BitmapFactory.decodeResource(context.getResources(),data.get(position).getImg());
//        bitmap=Bitmap.createScaledBitmap(bitmap,250,250,false);
        try {
            Picasso.with(context)
                    .load(data.get(position).getImg())
                    .into(img);

//            img.setImageBitmap(data.get(position).getImg());
//            img.setImageBitmap(bitmap);
        }catch (OutOfMemoryError e){
            recycleHalf();
           System.gc();
            return getView(position,convertView,parent);
        }
        mRecycleList.add(new WeakReference<View>(img));

        return convertView;
    }
    public void recycleHalf() {
        int halfSize = mRecycleList.size() / 2;
        List<WeakReference<View>> recycleHalfList = mRecycleList.subList(0, halfSize);

        RecycleUtils.recursiveRecycle(recycleHalfList);

        for (int i = 0; i < halfSize; i++)
            mRecycleList.remove(0);
    }

    public void recycle() {
        RecycleUtils.recursiveRecycle(mRecycleList);
    }


}
