package com.example.jisung.herh;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private  long lastTimeBackPressed;
    ArrayList<Store> stores = new ArrayList<>();
    ArrayList<Store> rests = new ArrayList<>();
    storeAdapter adapter;
    storeAdapter resadapter;
    GridView list;
    private String user_id;
    ImageButton pub;
    ImageButton rest;
    ViewPager eventSlide;
    int slidNum=0;
    Fragment cur_fragment=new Fragment();

    Handler mHandler=new Handler(){
        public void handleMessage(Message msg){
            if(slidNum==5)
                slidNum=0;
            eventSlide.setCurrentItem(slidNum++);
            mHandler.sendEmptyMessageDelayed(0,3000);
        }
    };


    protected void init(){
        Intent intent = getIntent();
        user_id = intent.getStringExtra("id");
        pub = (ImageButton) findViewById(R.id.pub);
        rest = (ImageButton) findViewById(R.id.rest);
        eventSlide = (ViewPager) findViewById(R.id.eventSlide);
        Log.d("test11","-1");

        eventSlide.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        Log.d("test11","0");

        eventSlide.setCurrentItem(0);
        Log.d("test11","5");

    }


    protected void storeGridSet() {
        list = (GridView) findViewById(R.id.stores);
        adapter = new storeAdapter(this, stores);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("store", stores.get(position).getStore_name());
                intent.putExtra("image", stores.get(position).getImg());
                intent.putExtra("id", user_id);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        pub.setOnClickListener(colorChanger);
        rest.setOnClickListener(colorChanger);

        stores.add(new Store(R.drawable.sample2,"한신포차"));
        stores.add(new Store(R.drawable.sample3,"맥주창고"));
        stores.add(new Store(R.drawable.sample4,"투다리"));
        stores.add(new Store(R.drawable.sample5,"봉구비어"));
        stores.add(new Store(R.drawable.sample1,"칠성포차"));


        rests.add(new Store(R.drawable.sample4, "덴뿌라"));
        rests.add(new Store(R.drawable.sample5, "난집에 돈까스"));

        storeGridSet();
        mHandler.sendEmptyMessage(0);
    }

    View.OnClickListener colorChanger = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.pub){
                pub.setBackgroundResource(R.color.loginBtnOn);
                rest.setBackgroundResource(R.color.loginButton);
                adapter = new storeAdapter(MainActivity.this, stores);

            }
            else if(v.getId() == R.id.rest){
                pub.setBackgroundResource(R.color.loginButton);
                rest.setBackgroundResource(R.color.loginBtnOn);
                adapter = new storeAdapter(MainActivity.this, rests);

            }
            list.setAdapter(adapter);
        }
    };

    public void onClick(View v){

    }

    public void menuClick(View v){
        Intent intent = new Intent(MainActivity.this, MymenuActivity.class);
        intent.putExtra("id", user_id);
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

    private class pagerAdapter extends FragmentStatePagerAdapter {


        public pagerAdapter(android.support.v4.app.FragmentManager fm){
            super(fm);
            Log.d("test11","1");

        }

        @Override
        public android.support.v4.app.Fragment getItem(int position){
            Log.d("test11","2");

            switch (position){
                case 0:
                    Log.d("test11","3");
                    cur_fragment=new page1(R.drawable.sample1);
                    return cur_fragment;
                case 1:
                    cur_fragment=new page1(R.drawable.sample2);
                    return cur_fragment;
                case 2:
                    cur_fragment=new page1(R.drawable.sample3);
                    return cur_fragment;
                case 3:
                    cur_fragment=new page1(R.drawable.sample4);
                    return cur_fragment;
                case 4:
                    cur_fragment=new page1(R.drawable.sample5);
                    return cur_fragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount(){
            return 5;
        }
    }
}
