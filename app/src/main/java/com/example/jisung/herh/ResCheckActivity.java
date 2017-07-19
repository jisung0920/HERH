package com.example.jisung.herh;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ResCheckActivity extends AppCompatActivity {

    String myJSON;

    ArrayList<Menu> alist,alist2;
    MenuAdapter m_adapter,m_adapter2;

    CalendarView c1;
    Button resBtn,reqBtn;
    ListView listview,list,list2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_check);
        init();

        c1 = (CalendarView)findViewById(R.id.calendarView);
        c1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                View r_view = View.inflate(view.getContext(), R.layout.r_list, null);
                getDbData("http://jisung0920.cafe24.com/hers.php");
                final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setView(r_view);
                listview = (ListView) r_view.findViewById(R.id.list_item);
                TextView s_date = (TextView)r_view.findViewById(R.id.textView2);
                s_date.setText(year + " . " + (month+1) + " . " + dayOfMonth);
                Button button = (Button)r_view.findViewById(R.id.button);
                button.setVisibility(View.GONE);
                m_adapter = new MenuAdapter(r_view.getContext(), alist);
                listview.setAdapter(m_adapter);
                dialog.show();
            }
        });
        m_adapter2 = new MenuAdapter(this,alist2);
        Menu menu2 = new Menu("test","test","test");

        alist2.add(menu2);
        list2.setAdapter(m_adapter2);
        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View re_view = View.inflate(view.getContext(), R.layout.r_list_item2,null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setView(re_view);
                Button button = (Button)re_view.findViewById(R.id.callbutton);
                dialog.show();
            }
        });
    }
    void init(){
        resBtn = (Button)findViewById(R.id.resBtn);
        reqBtn = (Button)findViewById(R.id.reqBtn);
        list = (ListView)findViewById(R.id.list1);
        alist = new ArrayList<Menu>();
        alist2 = new ArrayList<Menu>();
        list2 = (ListView)findViewById(R.id.list1);

    }
    public void onClick(View v){
        if(v.getId()==R.id.resBtn){
            resBtn.setBackgroundResource(R.color.loginBtnOn);
            reqBtn.setBackgroundResource(R.color.loginButton);
            c1.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
        else if(v.getId()==R.id.reqBtn){
            reqBtn.setBackgroundResource(R.color.loginBtnOn);
            resBtn.setBackgroundResource(R.color.loginButton);
            list.setVisibility(View.VISIBLE);
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
                    alist.clear();
                    for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                        JSONObject object = reserveData.getJSONObject(i);
                        String name = object.getString("user_name");
                        String num = object.getString("number");
                        String tel = object.getString("user_tel");
                        String time = object.getString("time");
                        Menu menu = new Menu(time,name,num);
                        alist.add(menu);
                    }
                    m_adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }
}