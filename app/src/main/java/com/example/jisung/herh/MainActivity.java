package com.example.jisung.herh;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Store> stores = new ArrayList<>();
    storeAdapter adapter;
    GridView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stores.add(new Store(R.drawable.sample2,"한신포차"));
        stores.add(new Store(R.drawable.sample3,"맥주창고"));
        stores.add(new Store(R.drawable.sample4,"투다리"));
        stores.add(new Store(R.drawable.sample5,"봉구비어"));
        list = (GridView)findViewById(R.id.stores);
        adapter = new storeAdapter(this,stores);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                intent.putExtra("store",stores.get(position).getStore_name());
                intent.putExtra("image", stores.get(position).getImg());
                startActivity(intent);
            }
        });
    }
    public void onClick(View v){

    }
}
