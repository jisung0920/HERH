package com.example.jisung.herh;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private  long lastTimeBackPressed;
    ArrayList<Store> stores = new ArrayList<>();
    storeAdapter adapter;
    GridView list;
    BackPressCloseHandler backPressCloseHandler;
    private String id;

    protected void init(){
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        backPressCloseHandler = new BackPressCloseHandler(this);
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

    public void menuClick(View v){
        Intent intent = new Intent(MainActivity.this, MymenuActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finishAffinity();
            return;
        }
        Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

}
