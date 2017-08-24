package com.example.jisung.herh;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.media.Image;
import android.os.AsyncTask;
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

import com.google.android.gms.maps.MapFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class MainActivity extends AppCompatActivity{
    private  long lastTimeBackPressed;
    int pushFlag=0;
    final int STORE =0,REST = 1;
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
            mHandler.sendEmptyMessageDelayed(0,2500);
        }
    };


    protected void init(){
        Intent intent = getIntent();
        user_id = intent.getStringExtra("id");
        pub = (ImageButton) findViewById(R.id.pub);
        rest = (ImageButton) findViewById(R.id.rest);
        list = (GridView) findViewById(R.id.stores);
        adapter = new storeAdapter(this, stores);
        resadapter = new storeAdapter(this,rests);


        eventSlide = (ViewPager) findViewById(R.id.eventSlide);
        eventSlide.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        eventSlide.setCurrentItem(0);
        Log.d("test11","5");

    }


    protected void storeGridSet() {

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                if(pushFlag==STORE)
                    intent.putExtra("store", stores.get(position).getStore_name());
                else if(pushFlag==REST)
                    intent.putExtra("store", rests.get(position).getStore_name());
                intent.putExtra("id", user_id);

                startActivity(intent);

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!NetworkCheck.connect(this)) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        init();
        list.setAdapter(adapter);
        getStore("http://alpahers.cafe24.com/hers_store_a_list.php",1);
        getStore("http://alpahers.cafe24.com/hers_store_b_list.php",2);
        pub.setOnClickListener(colorChanger);
        rest.setOnClickListener(colorChanger);




        storeGridSet();
        mHandler.sendEmptyMessage(0);
    }

    View.OnClickListener colorChanger = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.pub){
                pub.setBackgroundResource(R.color.loginBtnOn);
                rest.setBackgroundResource(R.color.loginButton);
                list.setAdapter(adapter);
                pushFlag =STORE;
                adapter.notifyDataSetChanged();
            }
            else if(v.getId() == R.id.rest){
                pub.setBackgroundResource(R.color.loginButton);
                rest.setBackgroundResource(R.color.loginBtnOn);
               list.setAdapter(resadapter);
                pushFlag=REST;
                resadapter.notifyDataSetChanged();
            }
        }
    };


    public void menuClick(View v){
        if(!NetworkCheck.connect(this)) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
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
                    cur_fragment=new page1(1,MainActivity.this);
                    return cur_fragment;
                case 1:
                    cur_fragment=new page1(2,MainActivity.this);
                    return cur_fragment;
                case 2:
                    cur_fragment=new page1(3,MainActivity.this);
                    return cur_fragment;
                case 3:
                    cur_fragment=new page1(4,MainActivity.this);
                    return cur_fragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount(){
            return 4;
        }
    }



    private void getStore(final String string, final int c) { // 서버의 DB에서 data 가져오는 메소드
        class GetDataJSON extends AsyncTask<String, Void, String> { // 서버 관련이라 멀티 thread를 사용하기 위해
            // AsyncTask를 사용한다.
            @Override
            protected String doInBackground(String... params) {// AsyncTask의 overide 메소드

                String uri = params[0]; //params에 php 주소가 있으므로 uri에 주소가 들어간다.
                try {
                    URL url = new URL(uri);//url 객체가 생성된다.
                    HttpURLConnection con = (HttpURLConnection) url.openConnection(); //url을 연결하기 위한 객체 con을 생성
                    con.setConnectTimeout(3000);
                    con.setReadTimeout(3000);
                    InputStream inputStream = con.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String tmp;
                    StringBuilder stringBuilder = new StringBuilder(); //받아올 데이터들을 저장하기 위한 객체 생성

                    while ((tmp = bufferedReader.readLine()) != null) {//한줄씩 읽어온다.
                        stringBuilder.append(tmp + "\n");
                    }

                    bufferedReader.close();
                    inputStream.close();
                    con.disconnect();
                    Log.d("check11", stringBuilder.toString().trim());
                    return stringBuilder.toString().trim();//onPostExecute실행

                    //받아온 데이터를 StringBuilder 의 형태로 만든다.

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {// AsyncTask의 overide 메소드
                try {
                    JSONObject jsonObject = new JSONObject(result);//jsonObject 형태로 위에서 데이터들을 포멧한다.
                    JSONArray reserveData = jsonObject.getJSONArray("response"); //json형태에서 response라는 키를 갖는 배열을 가져온다.
                    long seed = System.nanoTime();
                    if(c==1) {
                        for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                            JSONObject object = reserveData.getJSONObject(i);
                            String store_name = object.getString("name");
                            String img_link = object.getString("img");

                            stores.add(new Store(img_link, store_name));

                        }
                        Collections.shuffle(stores,new Random(seed));
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                            JSONObject object = reserveData.getJSONObject(i);
                            String store_name = object.getString("name");
                            String img_link = object.getString("img");
                            rests.add(new Store(img_link, store_name));

                        }

                        Collections.shuffle(stores,new Random(seed));
                        resadapter.notifyDataSetChanged();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }
}
