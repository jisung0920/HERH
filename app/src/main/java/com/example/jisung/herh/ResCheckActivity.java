package com.example.jisung.herh;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

public class ResCheckActivity extends AppCompatActivity {

    ArrayList<reserver> list;
    ArrayList<reserver_al> r_list;
    reserverAdapter adapter;
    reserver_alAdapter r_adapter;
    String store,name,time;
    int flag=0;
    String dateText;
    int allow;
    CalendarView c1;
    Button resBtn,reqBtn;
    ListView listView,r_listView;
    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_check);
        init();
        Intent getintent = getIntent();
        store = getintent.getStringExtra("store");
        getDbResData("http://jisung0920.cafe24.com/hers_Res_C.php");
        TextView store_name = (TextView)findViewById(R.id.storeName);
        store_name.setText(store);
        c1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                View r_view = View.inflate(view.getContext(), R.layout.r_list, null);   //뷰 가져오기
                dateText =year + "-" + (month + 1) + "-" + dayOfMonth;
                getDbData("http://jisung0920.cafe24.com/hers.php"); //서버에서 데이터 가져오는 함수 호출
                final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext()); //대화상자 생성
                dialog.setView(r_view); //대화상자 뷰 설정
                listView = (ListView) r_view.findViewById(R.id.list_item); //리스트 뷰 가져오기
                TextView s_date = (TextView) r_view.findViewById(R.id.textView2); //택스트 뷰 가져오기
                s_date.setText(dateText); // 텍스트 뷰 설정
                Button button = (Button) r_view.findViewById(R.id.button); // 버튼 뷰 가져오기
                button.setVisibility(View.INVISIBLE);
                adapter = new reserverAdapter(r_view.getContext(), list); //어뎁터 생성 및 설정
                listView.setAdapter(adapter); //리스트뷰와 어뎁터 연결
                dialog.show(); // 대화상자 보여주기

            }

        });

        r_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View re_view = View.inflate(view.getContext(), R.layout.r_list_item2,null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
//                dialog.setView(re_view);
                TextView resName = (TextView) re_view.findViewById(R.id.res_name);
                TextView resTel = (TextView) re_view.findViewById(R.id.res_tel);
                TextView resTime = (TextView) re_view.findViewById(R.id.res_time);
                TextView resNum = (TextView) re_view.findViewById(R.id.res_num);
                resName.setText("Name : " + r_list.get(position).getName());
                resTel.setText("Phone : " + r_list.get(position).getTel());
                resTime.setText("Time : " + r_list.get(position).getTime());
                resNum.setText("People : " + r_list.get(position).getNum());
                Button accept = (Button) re_view.findViewById(R.id.res_acc);
                ImageButton call = (ImageButton) re_view.findViewById(R.id.callbutton);
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/" + r_list.get(position).getTel()));
                        startActivity(intent);
                    }
                });
                final DialogInterface exit = dialog.setView(re_view).show();
                accept.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        time = r_list.get(position).getTime();
                        name = r_list.get(position).getName();
                        allow = r_list.get(position).getAllow();
                        getDbaccData("http://jisung0920.cafe24.com/hers_allow_change.php");
                        if(flag == 1){
                            r_list.get(position).setAllow(1);
                        }
                        r_adapter.notifyDataSetChanged();
                        exit.dismiss();
                    }
                });
                Button refuse = (Button) re_view.findViewById(R.id.res_ref);
                refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time = r_list.get(position).getTime();
                        name = r_list.get(position).getName();
                        allow = r_list.get(position).getAllow();
                        getDbaccData("http://jisung0920.cafe24.com/hers_allow_change.php");
                        if(flag == 1){
                            r_list.get(position).setAllow(2);
                        }
                        r_adapter.notifyDataSetChanged();
                        exit.dismiss();
                    }
                });

            }
        });
    }
    void init(){
        backPressCloseHandler = new BackPressCloseHandler(this);
        resBtn = (Button)findViewById(R.id.resBtn);
        reqBtn = (Button)findViewById(R.id.reqBtn);
        list = new ArrayList<reserver>();
        c1 = (CalendarView) findViewById(R.id.calendarView);

        r_listView = (ListView)findViewById(R.id.list1);
        r_list = new ArrayList<reserver_al>();
        r_adapter = new reserver_alAdapter(this,r_list);
        r_listView.setAdapter(r_adapter);

    }
    public void onClick(View v){
        if(v.getId()==R.id.resBtn){
            resBtn.setBackgroundResource(R.color.loginBtnOn);
            reqBtn.setBackgroundResource(R.color.loginButton);
            c1.setVisibility(View.VISIBLE);
            r_listView.setVisibility(View.GONE);
        }
        else if(v.getId()==R.id.reqBtn){
            reqBtn.setBackgroundResource(R.color.loginBtnOn);
            resBtn.setBackgroundResource(R.color.loginButton);
            r_listView.setVisibility(View.VISIBLE);
            c1.setVisibility(View.GONE);
        }
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
                    String postData = "store=" + URLEncoder.encode(store) +"&date=" + URLEncoder.encode(dateText);
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
                    Log.d("check11",stringBuilder.toString().trim());
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
                    list.clear();
                    for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                        JSONObject object = reserveData.getJSONObject(i);
                        String name = object.getString("user_name");
                        Log.d("check11","array error1");
                        int num = object.getInt("number");
                        int error = object.getInt("error_number");
                        int allow = object.getInt("allow");
                        String tel = object.getString("user_tel");
                        String time = object.getString("time");
                        Log.d("check11","array error");
                        reserver res = new reserver(time,name,tel,num,error,allow);
                        list.add(res);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }
    private void getDbaccData(String string) { // 서버의 DB에서 data 가져오는 메소드
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
                    String postData = "store=" + URLEncoder.encode(store) +"&date=" + URLEncoder.encode(dateText)+"&userName=" + URLEncoder.encode(name) + "&time=" + URLEncoder.encode(time) + "&allow=" + URLEncoder.encode(allow+"");
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    InputStream inputStream = con.getInputStream();
                    String result = loginResult(inputStream);

                    inputStream.close();
                    con.disconnect();
                    return result;//onPostExecute실행

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
                    if(result.equals("SUC")) {
                        flag = 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }
    private void getDbResData(String string) { // 서버의 DB에서 data 가져오는 메소드
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

                    return stringBuilder.toString().trim();//onPostExecute실행

                    //받아온 데이터를 StringBuilder 의 형태로 만든다.

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);//jsonObject 형태로 위에서 데이터들을 포멧한다.
                    JSONArray reserveData = jsonObject.getJSONArray("response"); //json형태에서 response라는 키를 갖는 배열을 가져온다.
                    for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                        JSONObject object = reserveData.getJSONObject(i);
                        String name = object.getString("user_name");
                        Log.d("check11","array error1");
                        int num = object.getInt("number");
                        int error = object.getInt("error_number");
                        int allow = object.getInt("allow");
                        String tel = object.getString("user_tel");
                        String time = object.getString("time");
                        String date = object.getString("date");
                        Log.d("check11","array error");
                        reserver_al res = new reserver_al(time,name,tel,num,error,allow,date);
                        r_list.add(res);
                    }
                    r_adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }

    String loginResult(InputStream in) {
        String data = "";
        Scanner s = new Scanner(in);
        data += s.nextLine();
        s.close();
        return data;}
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

}