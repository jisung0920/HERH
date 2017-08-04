package com.example.jisung.herh;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    String store, user_id,tel_num;
    TextView store_name, tel, max_number, open_Time;
    TextView item[];
    MapFragment mapFragment;
    Double Lat = 0.0;
    Double Lon = 0.0;
    Geocoder mCoder;
    String add;
    FragmentManager fragmentManager;


    int img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        init();
        getDbData("http://jisung0920.cafe24.com/hers_s_info.php");


    }

    void init() {
        item = new TextView[20];
        for(int i=0;i<20;i++){
            int Rid = getResources().getIdentifier("item"+i,"id",this.getPackageName());
            item[i]=(TextView)findViewById(Rid);
        }
        store_name = (TextView) findViewById(R.id.title);
        tel = (TextView) findViewById(R.id.tel);
        max_number = (TextView) findViewById(R.id.maxNum);
        open_Time = (TextView) findViewById(R.id.openTime);

        add="";
        Intent getintent = getIntent();
        store = getintent.getStringExtra("store");
        user_id = getintent.getStringExtra("id");
        img = getintent.getIntExtra("image", R.drawable.sample1);
        store_name.setText(store);
        mCoder = new Geocoder(this);

    }

    void scriptSet(String str){
        String data[];
        data = str.split("/");
        for(int i=0;i<=19;i++){

            Log.d("test11",data[i]);
            item[i].setText(data[i]);
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        LatLng SEOUL = new LatLng(Lat, Lon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title(store);
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    private void getDbData(String string) { // 서버의 DB에서 data 가져오는 메소드
        class GetDataJSON extends AsyncTask<String, Void, String> { // 서버 관련이라 멀티 thread를 사용하기 위해
            // AsyncTask를 사용한다.
            @Override
            protected String doInBackground(String... params) {// AsyncTask의 overide 메소드

                String uri = params[0]; //params에 php 주소가 있으므로 uri에 주소가 들어간다.
                try {
                    URL url = new URL(uri);//url 객체가 생성된다.
                    HttpURLConnection con = (HttpURLConnection) url.openConnection(); //url을 연결하기 위한 객체 con을 생성
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    String postData = "store=" + URLEncoder.encode(store);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
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
                // 위의 작업이 끝날때 실행된다
                //doinbackgroud 의 return인 sb.toString().trim()이 result로 온다.
                try {
                    JSONObject jsonObject = new JSONObject(result);//jsonObject 형태로 위에서 데이터들을 포멧한다.
                    JSONArray reserveData = jsonObject.getJSONArray("response"); //json형태에서 response라는 키를 갖는 배열을 가져온다.

                    for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                        JSONObject object = reserveData.getJSONObject(i);
                        tel_num = object.getString("store_tel");
                        tel.setText(tel_num);
                        max_number.setText(object.getString("max_number"));
                        add = object.getString("store_address");
                        open_Time.setText(object.getString("open_Time"));
                        scriptSet(object.getString("script"));
                    }

                    //주소값을 통하여 로케일을 받아온다

                    List<Address> addr = mCoder.getFromLocationName(add, 1);
                    Lat = addr.get(0).getLatitude();
                    Lon = addr.get(0).getLongitude();
                    fragmentManager = getFragmentManager();
                    mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
                    mapFragment.getMapAsync(InfoActivity.this);

                    //해당 로케일로 좌표를 구성한다


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }
    void onClick(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/" +tel_num));
        startActivity(intent);
    }


    public void resClick(View v) {
        Intent intent = new Intent(InfoActivity.this, User_ResActivity.class);
        intent.putExtra("store", store);
        intent.putExtra("id", user_id);
        startActivity(intent);
    }
}
